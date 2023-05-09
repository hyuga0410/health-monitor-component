package io.github.hyuga0410.health.monitor.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ServiceMessage
 *
 * @author pengqinglong
 * @since 2021/12/1
 */
@Data
public class ServiceMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目
     */
    private String project;

    /**
     * IP
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 服务名
     */
    private String name;

    /**
     * 环境
     */
    private String environment;

    /**
     * JDK版本
     */
    private String jdkVersion;

}