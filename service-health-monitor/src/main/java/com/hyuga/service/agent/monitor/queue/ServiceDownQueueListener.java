package com.hyuga.service.agent.monitor.queue;

import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.json.JsonUtil;
import com.hyuga.service.agent.monitor.constants.MonitorConstants;
import com.hyuga.service.agent.monitor.dao.automapper.MonitorDao;
import com.hyuga.service.agent.monitor.enums.ProjectEnum;
import com.rabbitmq.client.Channel;
import io.github.hyuga0410.health.monitor.enums.StatusEnum;
import io.github.hyuga0410.health.monitor.model.ServiceLogModel;
import io.github.hyuga0410.health.monitor.model.ServiceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.hyuga.service.agent.monitor.config.MonitorRabbitMqConfig.SINGLE_LISTENER_CONTAINER;
import static io.github.hyuga0410.health.monitor.constants.ServiceHeartbeatKey.SERVICE_DOWN_QUEUE;

/**
 * 服务心跳下线队列监听
 *
 * @author hyuga
 * @since 2021-12-09
 */
@Slf4j
@Service
@Profile({MonitorConstants.DEV, MonitorConstants.PRE, MonitorConstants.PRO})
public class ServiceDownQueueListener extends AbstractQueueListener {

    @Resource
    private MonitorDao monitorDao;

    @RabbitHandler
    @RabbitListener(
            queuesToDeclare = @Queue(
                    value = SERVICE_DOWN_QUEUE
            ),
            containerFactory = SINGLE_LISTENER_CONTAINER)
    public void onMessage(@Header(AmqpHeaders.CHANNEL) Channel channel, Message message) throws Exception {
        final String messageStr = new String(message.getBody());
        final ServiceMessage serviceMessage = parse(messageStr);

        String environment = serviceMessage.getEnvironment();
        String ip = serviceMessage.getIp();
        Integer port = serviceMessage.getPort();
        String name = serviceMessage.getName();
        ProjectEnum project = ProjectEnum.valueOf(serviceMessage.getProject());

        String title = String.format("[%s][%s][%s][%s] ", project, environment.toUpperCase(), name, ip + ":" + port);

        QueueTaskProxy proxy = new QueueTaskProxy(message, channel)
                .setLogStart(title + "service down message receive:" + JsonUtil.toJsonString(serviceMessage))
                .setLogInfo(title + "service down message receive success.")
                .setLogError(title + "service down message receive error.")
                .setLogEnd(title + "service down message receive end.");

        proxy.doTask(() -> {
            if (isSingleTest(serviceMessage)) {
                return;
            }

            final ServiceLogModel history = selectHistory(serviceMessage);

            if (ObjectUtil.isNull(history)) {
                return;
            }

            final Date now = DateUtil.now();
            history.setStatus(StatusEnum.OFFLINE);
            history.setOnlineDuration(DateUtil.intervalTimeDifferenceByCN(history.getServerStartTime(), now));
            history.setLastOnlineTime(now);
            monitorDao.updateStatus(history);

            sendNotify(StatusEnum.OFFLINE, message, serviceMessage, history.getOnlineDuration());
        });
    }

}
