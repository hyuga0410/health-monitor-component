package com.hyuga.service.agent.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 监控服务端配置
 *
 * @author hyuga
 * @since 2021-11-26 09:42:37
 */
@Data
@Configuration
@ConfigurationProperties(prefix = MonitorServerProperties.PREFIX)
public class MonitorServerProperties {

    public static final String PREFIX = "kfang.infra.monitor.server";

    /**
     * 心跳超时时间 单位分钟 默认5分钟
     */
    private int heartbeatTimeOut = 5;

    /**
     * 是否打开警报 默认不开启
     */
    private boolean enableWarn = false;

    /**
     * 是否打开过期数据清理
     */
    private boolean enableCleanData = false;

    /**
     * 清理过期最后在线时间超过x天的数据 单位 天 默认30天
     */
    private int cleanDataDay = 30;

    public int getCleanDataDay() {
        return ~cleanDataDay + 1;
    }
}
