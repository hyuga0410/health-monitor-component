package com.hyuga.service.agent.monitor.service;

import com.hyuga.service.agent.monitor.ServiceMonitorApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = ServiceMonitorApplication.class)
class FeishuServiceTest {

    @Resource
    private FeishuService feishuService;

    @Test
    void sendFeishu() {
        feishuService.sendFeishu("test", "context");
    }

}