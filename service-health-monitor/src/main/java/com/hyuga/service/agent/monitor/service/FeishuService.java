package com.hyuga.service.agent.monitor.service;

import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.http.feishu.FeishuUtil;
import cn.hyugatool.system.NetworkUtil;
import io.github.hyuga0410.health.monitor.enums.StatusEnum;
import kfang.agent.feature.saas.constants.SaasConstants;
import kfang.infra.common.KfangInfraCommonProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * 微信服务
 *
 * @author hyuga
 * @since 2021-12-13 09:20:41
 */
@Service
public class FeishuService {

    @Resource
    private Environment environment;
    @Resource
    private KfangInfraCommonProperties kfangInfraCommonProperties;

    private static final String SECRET = "secret";
    private static final String ACCESS_TOKEN = "accessToken";

    private static final String NOTIFY_TEMPLATE = "- 系统 : %s\n- 地址 : %s\n- 端口 : %s\n- 时间 : %s\n- %s\n%s";

    /**
     * SpringBoot端口配置KEY
     */
    private static final String SERVER_PORT = "server.port";

    public void sendUpOrDown(String env, String ip, Integer port, String name, String timestamp, StatusEnum status, String duration) {
        String statusDesc = ObjectUtil.equals(StatusEnum.ONLINE, status) ? "上线" : "离线";
        String durationStr = StringUtil.EMPTY;
        if (StringUtil.hasText(duration)) {
            if (StatusEnum.isOnline(status)) {
                durationStr += "距上次服务离线已经过了: " + duration;
            }
            if (StatusEnum.isOffline(status)) {
                durationStr += "本次服务总持续时长: " + duration;
            }
        }

        String title = String.format("%s - 服务 - %s", env.toUpperCase(), statusDesc);
        String text = String.format(NOTIFY_TEMPLATE, name, ip, port, timestamp, durationStr, getMonitorViewUrl(env));

        sendFeishu(title, text);
    }

    public void sendFeishu(String title, String context) {
        Properties feishuFileProperties = getFeishuProperties();
        String accessTokenOfFeishu = feishuFileProperties.getProperty(ACCESS_TOKEN);
        String secretOfFeishu = feishuFileProperties.getProperty(SECRET);
        String text = String.format("%s ⚠️️\n%s", title, context);
        FeishuUtil.sendText(accessTokenOfFeishu, secretOfFeishu, text);
    }

    public String getMonitorViewUrl(String env) {
        return String.format("http://%s:%s/monitor/server/view?env=%s", NetworkUtil.getLocalIpAddr(), environment.getProperty(SERVER_PORT), env.toLowerCase());
    }

    private Properties getFeishuProperties() {
        Properties properties = new Properties();

        boolean isDev = StringUtil.equalsIgnoreCase(SaasConstants.DEV, kfangInfraCommonProperties.getEnv().getDeploy());
        if (isDev) {
            properties.setProperty(ACCESS_TOKEN, "2b5b9e19-c123-4ed2-be84-bb08a61a30e6");
            properties.setProperty(SECRET, "97HAM55jxAHhnNvt6VPjj");
        } else {
            properties.setProperty(ACCESS_TOKEN, "adb897e0-92fa-4b21-bd38-f829b76e9e15");
            properties.setProperty(SECRET, "x4pVFhhhCC8U72hd0pFief");
        }
        return properties;
    }

}
