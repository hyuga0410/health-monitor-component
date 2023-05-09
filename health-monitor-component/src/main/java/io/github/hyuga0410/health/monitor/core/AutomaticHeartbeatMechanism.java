package io.github.hyuga0410.health.monitor.core;

import io.github.hyuga0410.health.monitor.core.processor.MonitorCoreProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 自动心跳机制
 *
 * @author 彭清龙
 * @since 2020-04-07 上午 9:13
 */
@Slf4j
@Component
@EnableScheduling
public class AutomaticHeartbeatMechanism implements ApplicationRunner, DisposableBean {

    @Resource
    private MonitorCoreProcessor monitorCoreProcessor;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        monitorCoreProcessor.serviceUp();
    }

    /**
     * Heartbeat links are sent every minute.
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void heartbeat() {
        monitorCoreProcessor.heartbeat();
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     */
    @Override
    public void destroy() {
        monitorCoreProcessor.serviceDown();
    }

}
