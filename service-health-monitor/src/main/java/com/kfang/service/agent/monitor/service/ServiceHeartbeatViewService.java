package com.kfang.service.agent.monitor.service;

import cn.hyugatool.core.collection.CollectionUtil;
import cn.hyugatool.core.collection.ListSortUtil;
import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.number.NumberUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.queue.FifoQueue;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.system.NetworkUtil;
import com.kfang.service.agent.monitor.constants.MonitorConstants;
import com.kfang.service.agent.monitor.dao.automapper.MonitorDao;
import com.kfang.service.agent.monitor.enums.ProjectEnum;
import io.github.hyuga0410.health.monitor.enums.StatusEnum;
import io.github.hyuga0410.health.monitor.model.ServiceInfo;
import io.github.hyuga0410.health.monitor.model.ServiceLogModel;
import kfang.infra.common.KfangInfraCommonProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务心跳视图Service
 *
 * @author hyuga
 * @since 2021-12-09 15:43:20
 */
@Service
public class ServiceHeartbeatViewService {

    /**
     * 开发环境
     */
    private static final String DEV = "DEV";
    /**
     * 测试环境
     */
    public static final String TEST = "TEST";
    /**
     * 预发布环境
     */
    private static final String PRE = "PRE";
    /**
     * 生产环境
     */
    private static final String PRO = "PRO";
    /**
     * 按情况划分为开发&测试环境or预发布&生产环境
     */
    private static final String ALL = "ALL";
    private static final String SERVICE_LABEL = "service-";
    private static final String TEST_ENV_PREFIX = "10.210.10.";

    @Resource
    private MonitorDao monitorDao;
    @Resource
    private KfangInfraCommonProperties kfangInfraCommonProperties;

    private String getTdStr(StatusEnum status) {
        if (StatusEnum.isOnline(status)) {
            return " 地址: %s\n 端口: %d\n 当前状态: %s\n 启动时间: %s\n 最后在线时间: %s\n 当前服务持续时长: %s";
        } else {
            return " 地址: %s\n 端口: %d\n 当前状态: %s\n 启动时间: %s\n 最后在线时间: %s\n 距服务离线已过去: %s";
        }
    }

    @SuppressWarnings("all")
    public String getHtml(String envUpperCase, String project, boolean single) {
        List<String> ips = ListUtil.newArrayList();
        String currentEnvUpperCase = kfangInfraCommonProperties.getEnv().getDeploy().toUpperCase();

        if (DEV.equals(currentEnvUpperCase)) {
            if (StringUtil.equals(DEV, envUpperCase)) {
                ips = MonitorConstants.DEVELOPER_LOCAL_ENVIRONMENT_IPS;
            } else if (StringUtil.equals(TEST, envUpperCase)) {
                // envUpperCase = DEV;
                ips = MonitorConstants.TEST_ENVIRONMENT_IPS;
            }
        }

        String envStr = getEnvStr(envUpperCase, currentEnvUpperCase);
        List<ServiceInfo> serviceList = this.getServiceList(envUpperCase, project, ips);

        if (ListUtil.isEmpty(serviceList)) {
            return "<html><body>暂无服务.</body></html>";
        }

        serviceList.sort(Comparator.comparingInt(ServiceInfo::getPort));
        if (single) {
            ListSortUtil.reverse(serviceList);
        }

        StringBuilder sb = new StringBuilder("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <link rel=\"icon\" type=\"image/x-icon\" href=\"images/Favicon.ico\"/>")
                .append("    <title>服务监控</title>\n")
                .append("    <style type=\"text/css\">\n")
                .append("        .ONLINE{ color: green}\n")
                .append("        .OFFLINE{ color: red}\n")
                .append("        .black{ color: black;}\n")
                .append("        .title{font-weight: bold;}\n")
                .append("        table td {padding:5px; !import}\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n");

        StringBuffer projectBufferDev = StringUtil.buffer();
        String currentDevName = String.format("%s-%s", envStr, ProjectEnum.valueOf(project).getDesc());
        sb.append(String.format("<div class='title' style='color:#80007b;padding-left:5px;width:300px;'>当前环境：「<span style='color:red;'>%s</span>」</div>\n", currentDevName));
        sb.append("<div style='padding:10px;color:blue;line-height:30px;float:left;width:160px;float:left;'>");

        FifoQueue<String> devColorFifoQueue = initDevColor();
        FifoQueue<String> proColorFifoQueue = initProColor();
        Arrays.stream(ProjectEnum.values()).forEach(projectEnum -> {
            String color = devColorFifoQueue.get();
            projectBufferDev.append(String.format("<div><a style='color:red;' href='http://10.210.10.54:13998/monitor/server/view?env=all&project=%s&single=true'>#</a>", projectEnum.name()));
            projectBufferDev.append(String.format(" [<a style='color:%s;' href='http://10.210.10.54:13998/monitor/server/view?env=all&project=%s'><span>开发&测试-%s</span></a>]</div>", color, projectEnum.name(), projectEnum.getDesc()));
            projectBufferDev.append(String.format("<div> - [<a style='color:%s;' href='http://10.210.10.54:13998/monitor/server/view?env=dev&project=%s'><span>开发环境-%s</span></a>]</div>", color, projectEnum.name(), projectEnum.getDesc()));
            projectBufferDev.append(String.format("<div> - [<a style='color:%s;' href='http://10.210.10.54:13998/monitor/server/view?env=test&project=%s'><span>测试环境-%s</span></a>]</div>", color, projectEnum.name(), projectEnum.getDesc()));
        });
        sb.append(projectBufferDev);
        sb.append("</div>\n").append("<div style='padding:10px;color:blue;line-height:30px;float:left;'>");
        StringBuffer projectBufferPro = StringUtil.buffer();
        Arrays.stream(ProjectEnum.values()).forEach(projectEnum -> {
            String color = proColorFifoQueue.get();
            projectBufferPro.append(String.format("<div><a style='color:red;' href='http://172.24.16.48:13998/monitor/server/view?env=all&project=%s&single=true'>#</a>", projectEnum.name()));
            projectBufferPro.append(String.format(" [<a style='color:%s;' href='http://172.24.16.48:13998/monitor/server/view?env=all&project=%s'><span>预发布&生产-%s</span></a>]</div>", color, projectEnum.name(), projectEnum.getDesc()));
            projectBufferPro.append(String.format("<div> - [<a style='color:%s;' href='http://172.24.16.48:13998/monitor/server/view?env=pre&project=%s'><span>预发布环境-%s</span></a>]</div>", color, projectEnum.name(), projectEnum.getDesc()));
            projectBufferPro.append(String.format("<div> - [<a style='color:%s;' href='http://172.24.16.48:13998/monitor/server/view?env=pro&project=%s'><span>生产环境-%s</span></a>]</div>", color, projectEnum.name(), projectEnum.getDesc()));
        });
        sb.append(projectBufferPro);
        sb.append("</div>\n");
        appendDevelopersIps(sb, currentEnvUpperCase);
        sb.append("<table style='border-left: 1px solid;padding-left:20px;'>\n");
        CollectionUtil.forEach(serviceList, (i, serviceInfo) -> {
            sb.append("<tr>\n");

            int port = serviceInfo.getPort();
            String name = serviceInfo.getName();
            String jdkVersion = serviceInfo.getJdkVersion();
            String index = NumberUtil.zeroPaddingOfFront(i + 1, 2);

            if (name.contains(SERVICE_LABEL)) {
                if (single) {
                    sb.append(String.format("<td class='title'>%s</td>\n", name));
                } else {
                    String format = "<td class='title'>[%s] <span style='color:green;'>[%s] </span><span style='color:blueviolet;'>[%s]</span> [%s]</td>\n";
                    sb.append(String.format(format, index, port, jdkVersion, name));
                }
            } else {
                if (single) {
                    sb.append(String.format("<td class='title'>%s</td>\n", name));
                } else {
                    String format = "<td class='title'>[%s] <span style='color:#005670;'>[%s] </span> <span style='color:blueviolet;'>[%s]</span> [%s]</td>\n";
                    sb.append(String.format(format, index, port, jdkVersion, name));
                }
            }

            List<ServiceInfo.ServiceIpInfo> ipServiceInfoList = serviceInfo.getIpServiceInfoList();
            ipServiceInfoList.sort(Comparator.comparingInt(ServiceInfo.ServiceIpInfo::getIpTail));

            if (ListUtil.isEmpty(ipServiceInfoList) || single) {
                sb.append("<td>\n");
                sb.append("</td>\n");
            } else {
                for (ServiceInfo.ServiceIpInfo item : ipServiceInfoList) {
                    String offlineDuration = DateUtil.intervalTimeDifferenceByCN(item.getLastOnlineTime(), DateUtil.now());
                    StatusEnum status = item.getStatus();
                    String title = String.format(getTdStr(status),
                            item.getIp(),
                            item.getPort(),
                            status.getDesc(),
                            DateUtil.format(item.getServerStartTime()),
                            DateUtil.format(item.getLastOnlineTime()),
                            StatusEnum.isOnline(status) ? item.getOnlineDuration() : offlineDuration);
                    if (StatusEnum.isOnline(status)) {
                        sb.append(String.format("<td><div class='ONLINE title' title=\"%s\">[%s]</div></td>\n", title, item.getIpTail()));
                    } else {
                        sb.append(String.format("<td><div class='OFFLINE title' title=\"%s\">[%s]</div></td>\n", title, item.getIpTail()));
                    }
                }
            }
            sb.append("</tr>\n");
        });

        sb.append("</table>\n");
        sb.append("</body>\n");
        sb.append("</html>");
        return sb.toString();
    }

    private void appendDevelopersIps(StringBuilder sb, String currentEnvUpperCase) {
        if (!DEV.equals(currentEnvUpperCase)) {
            return;
        }
        sb.append("\n");
        sb.append("<div style='position: fixed;top: 0;right: 0;padding:10px;color:cornflowerblue;background-color:black'>\n");

        Map<String, String> testEnvironmentIpUser = MonitorConstants.TEST_ENVIRONMENT_IP_USER;
        TreeMap<Integer, String> testTree = new TreeMap<>();
        testEnvironmentIpUser.forEach((ip, userName) -> {
            int ipTail = Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1));
            testTree.put(ipTail, userName);
        });

        Map<String, String> developerLocalEnvironmentIpUser = MonitorConstants.DEVELOPER_LOCAL_ENVIRONMENT_IP_USER;
        TreeMap<Integer, String> developerTree = new TreeMap<>();
        developerLocalEnvironmentIpUser.forEach((ip, userName) -> {
            int ipTail = Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1));
            developerTree.put(ipTail, userName);
        });

        sb.append("<span style='font-weight:bold;color:aliceblue;'>测试环境</span><br>");
        testTree.forEach((ipTail, userName) -> sb.append(String.format("<span>[%s] [%s]", ipTail, userName)).append("</span><br>"));
        sb.append("<br><span style='font-weight:bold;color:aliceblue;'>开发人员</span><br>");
        developerTree.forEach((ipTail, userName) -> sb.append(String.format("<span>[%s] [%s]", ipTail, userName)).append("</span><br>"));
        sb.append("</div>");
    }

    private FifoQueue<String> initProColor() {
        FifoQueue<String> proColorFifoQueue = new FifoQueue<>();
        proColorFifoQueue.put("black");
        proColorFifoQueue.put("#005670");
        proColorFifoQueue.put("chocolate");
        proColorFifoQueue.put("green");
        return proColorFifoQueue;
    }

    private FifoQueue<String> initDevColor() {
        FifoQueue<String> devColorFifoQueue = new FifoQueue<>();
        devColorFifoQueue.put("black");
        devColorFifoQueue.put("#005670");
        devColorFifoQueue.put("chocolate");
        devColorFifoQueue.put("green");
        return devColorFifoQueue;
    }

    private String getEnvStr(String envUpperCase, String currentEnvUpperCase) {
        if (StringUtil.equals(TEST, envUpperCase)) {
            return "测试环境";
        }
        switch (currentEnvUpperCase) {
            case DEV:
                if (StringUtil.equals(ALL, envUpperCase)) {
                    return "开发&测试环境";
                } else if (StringUtil.equals(DEV, envUpperCase)) {
                    return "开发环境";
                }
            case PRE:
                if (StringUtil.equals(ALL, envUpperCase)) {
                    return "预发布&生产环境";
                } else if (StringUtil.equals(PRE, envUpperCase)) {
                    return "预发布环境";
                } else if (StringUtil.equals(PRO, envUpperCase)) {
                    return "生产环境";
                }
            default:
                return StringUtil.EMPTY;
        }
    }

    private List<ServiceInfo> getServiceList(String env, String project, List<String> ips) {
        if (StringUtil.equals(TEST, env) && NetworkUtil.getLocalIpAddr().startsWith(TEST_ENV_PREFIX)) {
            env = DEV;
        }
        ServiceLogModel query = new ServiceLogModel();
        query.setEnvironment(env);
        query.setIps(ips);
        query.setProject(ProjectEnum.valueOf(project).name());
        List<ServiceLogModel> list = monitorDao.selectServiceLogList(query);
        if (ListUtil.isEmpty(list)) {
            return null;
        } else {
            list.forEach(serviceInfo -> {
                String name = serviceInfo.getName();
                String suffix = name.substring(name.lastIndexOf("-") + 1);
                if (NumberUtil.isNumber(suffix)) {
                    serviceInfo.setName(StringUtil.removeEnd(name, "-" + suffix));
                }
            });
        }
        List<ServiceInfo> result = ListUtil.newArrayList();
        // 以服务名分组
        Map<String, List<ServiceLogModel>> collect = list.stream().collect(Collectors.groupingBy(ServiceLogModel::getName));

        // 封装服务
        for (Map.Entry<String, List<ServiceLogModel>> entry : collect.entrySet()) {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setName(entry.getKey());
            ServiceLogModel serviceLogModel = ListUtil.findFirst(entry.getValue());
            serviceInfo.setPort(ObjectUtil.nonNull(serviceLogModel) ? serviceLogModel.getPort() : 0);
            ServiceLogModel lastModel = ListUtil.findLast(entry.getValue());
            serviceInfo.setJdkVersion(ObjectUtil.nonNull(lastModel) ? lastModel.getJdkVersion() : "11");
            // 封装服务item
            for (ServiceLogModel model : entry.getValue()) {
                ServiceInfo.ServiceIpInfo serviceIpInfo = new ServiceInfo.ServiceIpInfo(model);
                serviceInfo.getIpServiceInfoList().add(serviceIpInfo);
            }
            result.add(serviceInfo);
        }
        return result;
    }

}
