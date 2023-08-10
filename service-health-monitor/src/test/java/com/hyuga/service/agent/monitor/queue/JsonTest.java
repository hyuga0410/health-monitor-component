package com.hyuga.service.agent.monitor.queue;

import cn.hyugatool.core.string.StringUtil;
import cn.hyugatool.json.JsonUtil;
import com.alibaba.fastjson2.JSON;
import io.github.hyuga0410.health.monitor.model.ServiceMessage;
import org.junit.jupiter.api.Test;

/**
 * JsonTest
 *
 * @author hyuga
 * @since 2022/8/18 21:35
 */
public class JsonTest {

    @Test
    void test() {
        String message = "{\"environment\":\"DEV\",\"ip\":\"10.210.10.54\",\"name\":\"service-tuokeben-report-mng\",\"port\":33510,\"project\":\"TUOKEBEN\"}";
        System.out.println(StringUtil.formatString(JsonUtil.toJavaObject(message, ServiceMessage.class)));
        System.out.println(StringUtil.formatString(JSON.to(ServiceMessage.class, message)));
    }

}
