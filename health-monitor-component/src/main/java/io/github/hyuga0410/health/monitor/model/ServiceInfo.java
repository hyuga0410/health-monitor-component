package io.github.hyuga0410.health.monitor.model;

import io.github.hyuga0410.health.monitor.enums.StatusEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务信息
 *
 * @author hyuga
 * @since 2021-11-26 11:33:22
 */
public class ServiceInfo {

    /**
     * 项目
     */
    @Setter
    @Getter
    private String project;

    /**
     * 服务名
     */
    @Setter
    @Getter
    private String name;

    /**
     * 服务端口
     */
    @Setter
    @Getter
    private int port;

    /**
     * JDK版本
     */
    @Setter
    @Getter
    private String jdkVersion;

    /**
     * 服务对应多服务器集合
     */
    @Getter
    private final List<ServiceIpInfo> ipServiceInfoList = new ArrayList<>();

    @Data
    public static class ServiceIpInfo {

        /**
         * IP
         */
        private String ip;
        /**
         * 端口
         */
        private int port;
        /**
         * IP后缀
         */
        private int ipTail;
        /**
         * 服务状态
         */
        private StatusEnum status;
        /**
         * 服务开始时间
         */
        private Date serverStartTime;
        /**
         * 服务最后在线时间
         */
        private Date lastOnlineTime;
        /**
         * 在线时长
         */
        private String onlineDuration;
        /**
         * 离线时长
         */
        private String offlineDuration;
        /**
         * JDK版本
         */
        private String jdkVersion;

        public ServiceIpInfo(ServiceLogModel model) {
            this.ip = model.getIp();
            this.port = model.getPort();
            this.ipTail = Integer.parseInt(ip.substring(ip.lastIndexOf(".") + 1));
            this.status = model.getStatus();
            this.serverStartTime = model.getServerStartTime();
            this.lastOnlineTime = model.getLastOnlineTime();
            this.offlineDuration = model.getOfflineDuration();
            this.onlineDuration = model.getOnlineDuration();
            this.jdkVersion = model.getJdkVersion();
        }
    }

}
