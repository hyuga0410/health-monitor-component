package com.kfang.service.agent.monitor.service;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.date.DateUtil;
import com.kfang.service.agent.monitor.config.MonitorServerProperties;
import com.kfang.service.agent.monitor.constants.MonitorConstants;
import com.kfang.service.agent.monitor.dao.automapper.MonitorDao;
import io.github.hyuga0410.health.monitor.enums.StatusEnum;
import io.github.hyuga0410.health.monitor.model.ServiceLogModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * MonitorServerTask
 *
 * @author pengqinglong
 * @since 2021/12/2
 */
@Component
@EnableScheduling
@Slf4j
public class MonitorServerTask {

    @Resource
    private MonitorDao monitorDao;
    @Resource
    private FeishuService feishuService;
    @Resource
    private MonitorServerProperties monitorServerProperties;

    /**
     * 扫描在线服务心跳
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void scanHeartbeat() {
        // 查询所有在线服务
        List<ServiceLogModel> list = monitorDao.selectServiceLogOnlineAll();
        if (ListUtil.isEmpty(list)) {
            log.warn("monitor none online service!");
            return;
        }

        // 找寻心跳超时服务
        for (ServiceLogModel model : list) {
            final Date now = DateUtil.now();
            String env = model.getEnvironment();
            // 计算最后时间和现在相差了几分钟
            long timeout = DateUtil.minuteDifference(model.getLastOnlineTime(), now);

            int heartbeatTimeOut = monitorServerProperties.getHeartbeatTimeOut();
            if (timeout < heartbeatTimeOut) {
                // 未超时
                continue;
            }

            // 超时更新服务为离线且更新持续在线时长
            ServiceLogModel update = new ServiceLogModel();
            update.setId(model.getId());
            update.setStatus(StatusEnum.OFFLINE);
            update.setOnlineDuration(DateUtil.intervalTimeDifferenceByCN(model.getServerStartTime(), now));
            monitorDao.updateStatus(update);

            // 判断心跳超时的服务是否在线 如果在线 通过配置决定是否需要告警
            if (StatusEnum.isOnline(model) && monitorServerProperties.isEnableWarn()) {
                String ip = model.getIp();
                Integer port = model.getPort();
                String name = model.getName();
                if (MonitorConstants.DEVELOPER_LOCAL_ENVIRONMENT_IPS.contains(ip)) {
                    log.info(String.format("开发本地IP，疑似开发断点，不发送预警通知！IP:%s,PORT:%s,NAME:%s", ip, port, name));
                    return;
                }

                String text = "# %s - 服务 - 心跳异常\n- 系统: %s\n- 地址: %s\n- 端口: %s\n- 时间: %s\n";
                text += String.format("- 已超过 %s 分钟未收到心跳请求.", heartbeatTimeOut);
                text += "\n" + feishuService.getMonitorViewUrl(env);

                text = String.format(text, env.toUpperCase(), name, ip, port, DateUtil.todayTime());

                String title = String.format("%s - 服务 - 心跳异常", env);
                feishuService.sendFeishu(title, text);
            }
        }
        log.info("monitor server scan service list success~");
    }


    /**
     * 每天凌晨1点清理过期数据
     */
    @Scheduled(cron = "0 0 01 * * ?")
    public void cleanStaleData() {
        if (!monitorServerProperties.isEnableCleanData()) {
            log.info("monitor server clean data no enable!");
            return;
        }

        Date date = DateUtil.moreOrLessDays(new Date(), monitorServerProperties.getCleanDataDay());

        int i;
        do {
            // 防止参数调整太大导致一次删除太多数据导致数据库卡住 此处每次只删除500条 只删除离线且过期数据
            i = monitorDao.cleanStaleData(DateUtil.formatDateTime(date));
            log.info("monitor clean data count: {}", i);
        } while (i > 1);
        log.info("monitor server clean data success～");
    }

}