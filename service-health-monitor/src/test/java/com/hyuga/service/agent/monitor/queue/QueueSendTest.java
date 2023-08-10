package com.hyuga.service.agent.monitor.queue;

import cn.hyugatool.json.JsonUtil;
import com.hyuga.service.agent.monitor.ServiceMonitorApplication;
import io.github.hyuga0410.health.monitor.model.ServiceMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.UUID;

import static io.github.hyuga0410.health.monitor.constants.ServiceHeartbeatKey.SERVICE_UP_QUEUE;


/**
 * QueueSendTest
 *
 * @author hyuga
 * @since 2021-12-10 15:28:28
 */
@SpringBootTest(classes = ServiceMonitorApplication.class)
public class QueueSendTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send() {
        ServiceMessage serviceMessage = new ServiceMessage();
        serviceMessage.setIp("127.0.0.1");
        serviceMessage.setPort(3000);
        serviceMessage.setName("service-hyuga");
        serviceMessage.setEnvironment("DEV");

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        Message message = new Message(JsonUtil.toJsonString(serviceMessage).getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(SERVICE_UP_QUEUE, message);
        // rabbitTemplate.send(SERVICE_UP_QUEUE, message);
    }

}
