package io.github.hyuga0410.health.monitor.model;

import io.github.hyuga0410.health.monitor.enums.StatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ServiceLogModel
 *
 * @author pengqinglong
 * @since 2021/12/1
 */
@Data
@NoArgsConstructor
public class ServiceLogModel implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * IP
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 项目
     */
    private String project;

    /**
     * JDK版本
     */
    private String jdkVersion;

    /**
     * 服务名
     */
    private String name;

    /**
     * 环境
     */
    private String environment;

    /**
     * 服务状态
     */
    private StatusEnum status;

    /**
     * 服务启动时间
     */
    private Date serverStartTime;

    /**
     * 服务最后在线时间
     */
    private Date lastOnlineTime;

    /**
     * 离线时长
     */
    private String offlineDuration;

    /**
     * 在线时长
     */
    private String onlineDuration;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * IPS
     */
    private List<String> ips;

}