package io.github.hyuga0410.health.monitor.core.processor;

import com.alibaba.fastjson2.JSON;
import io.github.hyuga0410.health.monitor.MonitorConfiguration;
import io.github.hyuga0410.health.monitor.constants.MonitorConstant;
import io.github.hyuga0410.health.monitor.constants.ServiceHeartbeatKey;
import io.github.hyuga0410.health.monitor.model.ServiceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * 服务监控
 *
 * @author hyuga
 * @since 2021-11-26 10:18:11
 */
@Slf4j
@Component
public class MonitorCoreProcessor implements ApplicationContextAware {

    /**
     * 服务IP
     */
    private static final String IP = NetworkUtil.getLocalIpAddr();
    /**
     * 项目
     */
    private final String PROJECT = MonitorConfiguration.project();
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 服务端口
     */
    private int port;
    /**
     * 服务环境
     */
    private String[] deploys;
    /**
     * JDK版本
     */
    private static final String JDK_VERSION = System.getProperty(MonitorConstant.JAVA_VERSION);
    /**
     * RabbitMQ
     */
    private RabbitTemplate rabbitTemplate;

    public void serviceUp() {
        ServiceMessage message = new ServiceMessage();
        message.setProject(PROJECT);
        message.setIp(IP);
        message.setPort(port);
        message.setName(serviceName);
        message.setEnvironment(deploys[0].toUpperCase());
        message.setJdkVersion(JDK_VERSION);
        up(message);

        log.info("monitor client start success~");
    }

    public void heartbeat() {
        ServiceMessage message = new ServiceMessage();
        message.setProject(PROJECT);
        message.setIp(IP);
        message.setPort(port);
        message.setName(serviceName);
        message.setEnvironment(deploys[0].toUpperCase());
        heartbeat(message);

        log.info("monitor client heartbeat success~");
    }

    public void serviceDown() {
        ServiceMessage message = new ServiceMessage();
        message.setProject(PROJECT);
        message.setIp(IP);
        message.setPort(port);
        message.setName(serviceName);
        message.setEnvironment(deploys[0].toUpperCase());
        down(message);

        log.info("monitor client destroy success~");
    }

    private static Message getMessage(ServiceMessage serviceMessage) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(SnowflakeIdUtil.getSnowflakeIdStr());
        messageProperties.setTimestamp(new Date());

        return new Message(JSON.toJSONString(serviceMessage).getBytes(), messageProperties);
    }

    private void up(ServiceMessage serviceMessage) {
        send(ServiceHeartbeatKey.SERVICE_UP_QUEUE, serviceMessage);
    }

    private void heartbeat(ServiceMessage serviceMessage) {
        send(ServiceHeartbeatKey.SERVICE_HEARTBEAT_QUEUE, serviceMessage);
    }

    private void down(ServiceMessage serviceMessage) {
        send(ServiceHeartbeatKey.SERVICE_DOWN_QUEUE, serviceMessage);
    }

    private void send(String serviceHeartbeatKey, ServiceMessage serviceMessage) {
        Integer port = serviceMessage.getPort();
        boolean isJunitTest = Objects.equals(port, -1);
        if (isJunitTest) {
            return;
        }
        rabbitTemplate.convertAndSend(serviceHeartbeatKey, getMessage(serviceMessage));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Environment environment = applicationContext.getEnvironment();
        String serviceNameProperty = environment.getProperty(MonitorConstant.SERVER_NAME);
        serviceName = Objects.requireNonNull(serviceNameProperty).split("\\.")[0];
        port = Integer.parseInt(environment.getProperty(MonitorConstant.SERVER_PORT, "0"));
        deploys = environment.getActiveProfiles();
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
    }

}
