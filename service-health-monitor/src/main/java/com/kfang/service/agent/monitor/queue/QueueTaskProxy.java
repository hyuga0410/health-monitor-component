package com.kfang.service.agent.monitor.queue;

import cn.hyugatool.core.string.StringUtil;
import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.io.IOException;

/**
 * 队列任务代理类
 *
 * @author hyuga
 * @since 2021-12-10 14:10:12
 */
@Data
@Slf4j
@Accessors(chain = true)
public class QueueTaskProxy {

    public QueueTaskProxy(Message message, Channel channel) {
        this.message = message;
        this.channel = channel;
    }

    private Message message;
    private Channel channel;
    private String logStart;
    private String logEnd;
    private String logInfo;
    private String logError;

    /**
     * channel.basicAck(deliverTag, true); 消费成功，确认消息
     * channel.basicNack(deliverTag, false, true); nack返回false，出现异常并重新回到队列，重新消费
     * channel.basicReject(deliverTag, false); 为false则拒绝消息，丢掉该消息；为true会重新放回队列，重新消费
     */
    public void doTask(QueueTaskFunc queueTask) throws Exception {
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // final String receivedExchange = message.getMessageProperties().getReceivedExchange();
        // final String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();

        if (StringUtil.hasText(logStart)) {
            log.info(logStart);
        }
        try {
            queueTask.accept();
            if (StringUtil.hasText(logInfo)) {
                log.info(logInfo);
            }
            // 手动进行确认
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            e.printStackTrace();
            if (StringUtil.hasText(logError)) {
                log.error(logError + "#error:" + e.getMessage(), e);
            }
            try {
                // 手动进行确认
                channel.basicAck(deliveryTag, false);
                // 重新发送消息到队尾
                // channel.basicPublish(receivedExchange, receivedRoutingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBody());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                log.error(logError + "#message:" + ioException.getMessage(), ioException);
            }
            throw e;
        }
        if (StringUtil.hasText(logEnd)) {
            log.info(logEnd);
        }
    }

}
