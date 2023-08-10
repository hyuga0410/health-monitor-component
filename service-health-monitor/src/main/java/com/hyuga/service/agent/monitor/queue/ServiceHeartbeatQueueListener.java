package com.hyuga.service.agent.monitor.queue;

import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.json.JsonUtil;
import com.hyuga.service.agent.monitor.config.MonitorRabbitMqConfig;
import com.hyuga.service.agent.monitor.config.MonitorServerProperties;
import com.hyuga.service.agent.monitor.constants.MonitorConstants;
import com.hyuga.service.agent.monitor.dao.automapper.MonitorDao;
import com.hyuga.service.agent.monitor.enums.ProjectEnum;
import com.hyuga.service.agent.monitor.service.FeishuService;
import com.rabbitmq.client.Channel;
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

import static io.github.hyuga0410.health.monitor.constants.ServiceHeartbeatKey.SERVICE_HEARTBEAT_QUEUE;

/**
 * 服务心跳上线队列监听
 *
 * @author hyuga
 * @since 2021-12-09
 */
@Slf4j
@Service
@Profile({MonitorConstants.DEV, MonitorConstants.PRE, MonitorConstants.PRO})
public class ServiceHeartbeatQueueListener extends AbstractQueueListener {

    @Resource
    private MonitorDao monitorDao;
    @Resource
    private FeishuService feishuService;
    @Resource
    private MonitorServerProperties monitorServerProperties;

    @RabbitHandler
    @RabbitListener(
            queuesToDeclare = @Queue(
                    value = SERVICE_HEARTBEAT_QUEUE
            ),
            containerFactory = MonitorRabbitMqConfig.SINGLE_LISTENER_CONTAINER)
    public void onMessage(@Header(AmqpHeaders.CHANNEL) Channel channel, Message message) throws Exception {
        final String messageStr = new String(message.getBody());
        final ServiceMessage serviceMessage = parse(messageStr);

        String environment = serviceMessage.getEnvironment();
        String ip = serviceMessage.getIp();
        Integer port = serviceMessage.getPort();
        String name = serviceMessage.getName();
        ProjectEnum project = ProjectEnum.valueOf(serviceMessage.getProject());
        String jdkVersion = serviceMessage.getJdkVersion();

        String title = String.format("[%s][%s][%s][%s] ", project, environment.toUpperCase(), name, ip + ":" + port);

        QueueTaskProxy proxy = new QueueTaskProxy(message, channel)
                .setLogStart(title + "service heartbeat message receive:" + JsonUtil.toJsonString(serviceMessage))
                .setLogInfo(title + "service heartbeat message receive success.")
                .setLogError(title + "service heartbeat message receive error.")
                .setLogEnd(title + "service heartbeat message receive end.");

        proxy.doTask(() -> {
            if (isSingleTest(serviceMessage)) {
                return;
            }

            final ServiceLogModel history = selectHistory(serviceMessage);

            if (ObjectUtil.isNull(history)) {
                return;
            }

            // 心跳
            ServiceLogModel model = new ServiceLogModel();
            model.setId(history.getId());
            model.setJdkVersion(jdkVersion);
            model.setLastOnlineTime(new Date());
            model.setOnlineDuration(DateUtil.intervalTimeDifferenceByCN(history.getServerStartTime(), model.getLastOnlineTime()));
            monitorDao.updateLastOnlineTime(model);

            ifItIsRestoreHeartbeatAfterTimeout(history);
        });
    }

    private void ifItIsRestoreHeartbeatAfterTimeout(ServiceLogModel model) {
        final Date now = DateUtil.now();
        final String env = model.getEnvironment();
        final String ip = model.getIp();
        final Integer port = model.getPort();
        final String name = model.getName();

        // 计算最后时间和现在相差了几分钟
        long timeout = DateUtil.minuteDifference(model.getLastOnlineTime(), now);

        int heartbeatTimeOut = monitorServerProperties.getHeartbeatTimeOut();
        if (timeout < heartbeatTimeOut) {
            // 未超时
            return;
        }
        String text = "# %s - 服务 - 心跳恢复\n- 系统: %s\n- 地址: %s\n- 端口: %s\n- 时间: %s\n";
        text += String.format("- 已超过 %s 分钟心跳处于异常状态.\n", heartbeatTimeOut);
        text += "- 心跳恢复正常.";
        text += "\n" + feishuService.getMonitorViewUrl(env);

        text = String.format(text, env.toUpperCase(), name, ip, port, DateUtil.todayTime());

        String title = String.format("%s - 服务 - 心跳异常", env);
        feishuService.sendFeishu(title, text);
    }

}
