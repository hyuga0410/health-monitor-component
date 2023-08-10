package com.hyuga.service.agent.monitor;

import cn.hyugatool.core.lang.ConsoleAnsi;
import cn.hyugatool.extra.spring.HyugaSpringBeanPicker;
import cn.hyugatool.system.NetworkUtil;
import kfang.agent.feature.saas.mq.AgentMq;
import kfang.agent.feature.saas.sql.AgentSql;
import kfang.infra.common.logger.BootstrapLogDoneFilter;
import kfang.infra.feature.mysql.MysqlConfig;
import kfang.infra.service.ServiceBaseConfig;
import kfang.infra.web.WebBaseConfig;
import org.fusesource.jansi.Ansi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Import;

import javax.management.MalformedObjectNameException;


/**
 * ServiceMonitorApplication
 *
 * @author hyuga
 * @since 2021/5/31
 */
@SpringBootApplication(scanBasePackages = {"com.kfang.service.agent.monitor"})
@AgentSql
@AgentMq
@EnableHystrix
@Import({WebBaseConfig.class, ServiceBaseConfig.class, MysqlConfig.class, HyugaSpringBeanPicker.class})
public class ServiceMonitorApplication {

    public static void main(String[] args) throws MalformedObjectNameException {
        SpringApplication.run(ServiceMonitorApplication.class, args);
        BootstrapLogDoneFilter.bootstrapDone();

        ConsoleAnsi.init().color(Ansi.Color.RED).append(String.format("http://%s:%s/monitor/server/view", NetworkUtil.getLocalIpAddr(), NetworkUtil.getLocalPort())).print();
    }

}