package com.kfang.service.agent.monitor.queue;

import cn.hyugatool.core.date.DateUtil;
import cn.hyugatool.core.object.ObjectUtil;
import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.json.JsonUtil;
import com.kfang.service.agent.monitor.dao.automapper.MonitorDao;
import com.kfang.service.agent.monitor.service.FeishuService;
import io.github.hyuga0410.health.monitor.enums.StatusEnum;
import io.github.hyuga0410.health.monitor.model.ServiceLogModel;
import io.github.hyuga0410.health.monitor.model.ServiceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import javax.annotation.Resource;

/**
 * 抽象队列监听
 *
 * @author hyuga
 * @since 2021-12-09 18:23:27
 */
@Slf4j
public abstract class AbstractQueueListener {

    @Resource
    private MonitorDao monitorDao;
    @Resource
    private FeishuService feishuService;

    public ServiceMessage parse(String messageStr) {
        return JsonUtil.toJavaObject(messageStr, ServiceMessage.class);
    }

    public ServiceLogModel selectHistory(ServiceMessage serviceMessage) {
        final String ip = serviceMessage.getIp();
        final Integer port = serviceMessage.getPort();
        final String name = serviceMessage.getName();

        ServiceLogModel model = new ServiceLogModel();
        model.setIp(ip);
        model.setPort(port);
        model.setName(name);
        model.setEnvironment(serviceMessage.getEnvironment());
        // 查询数据
        ServiceLogModel historyLog = monitorDao.selectServiceLog(model);
        if (ObjectUtil.isNull(historyLog)) {
            log.warn("this service has not history log. name:{},ip:{},port:{}", name, ip, port);
        }
        return historyLog;
    }

    public boolean isSingleTest(ServiceMessage serviceMessage) {
        Integer port = serviceMessage.getPort();
        if (ObjectUtil.isNull(port)) {
            throw new RuntimeException("端口异常：端口号为空.");
        }
        return StringUtil.equals(port, "-1");
    }

    public void sendNotify(StatusEnum status, Message message, ServiceMessage serviceMessage, String duration) {
        String name = serviceMessage.getName();
        String ip = serviceMessage.getIp();
        Integer port = serviceMessage.getPort();
        String env = serviceMessage.getEnvironment();
        String timestamp = DateUtil.formatDateTime(message.getMessageProperties().getTimestamp());
        feishuService.sendUpOrDown(env, ip, port, name, timestamp, status, duration);
    }

}
