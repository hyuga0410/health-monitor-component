package com.kfang.service.agent.monitor.config;

import kfang.agent.feature.saas.mq.AgentRabbitMqConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

/**
 * RabbitMq配置
 *
 * @author hyuga
 * @date 2018-12-27
 */
@Slf4j
@Configuration
@Import({AgentRabbitMqConfiguration.class})
public class MonitorRabbitMqConfig {

    @Resource
    private CachingConnectionFactory connectionFactory;

    public static final String SINGLE_LISTENER_CONTAINER = "singleListenerContainer";

    /**
     * 消费者监听
     */
    @Bean(name = SINGLE_LISTENER_CONTAINER)
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new SimpleMessageConverter());

        // 单台并发消费者数量
        factory.setConcurrentConsumers(5);
        // 单台并发消费的最大消费者数量
        factory.setMaxConcurrentConsumers(10);
        // 预取消费数量,unchecked数量超过这个值broker将不会接收消息
        factory.setPrefetchCount(1);
        // 有事务时处理的消息数
        factory.setBatchSize(1);
        // 消息确认机制
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

}

