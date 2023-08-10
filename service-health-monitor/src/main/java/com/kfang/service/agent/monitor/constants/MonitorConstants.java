package com.kfang.service.agent.monitor.constants;

import cn.hyugatool.core.collection.ListUtil;
import cn.hyugatool.core.collection.MapUtil;

import java.util.List;
import java.util.Map;

/**
 * 常量
 *
 * @author hyuga
 * @since 2022-01-06 16:53:29
 */
public class MonitorConstants {

    public static final String DEV = "dev";
    public static final String PRE = "pre";
    public static final String PRO = "pro";

    public static final String ENV = "env";
    public static final String PROJECT = "project";
    public static final String SINGLE = "single";
    public static final String TEXT_HTML = "text/html";

    public final static Map<String, String> TEST_ENVIRONMENT_IP_USER = MapUtil.newHashMap();

    public final static Map<String, String> DEVELOPER_LOCAL_ENVIRONMENT_IP_USER = MapUtil.newHashMap();

    public static List<String> TEST_ENVIRONMENT_IPS;

    public static List<String> DEVELOPER_LOCAL_ENVIRONMENT_IPS;

    static {
        // 测试环境IP
        TEST_ENVIRONMENT_IP_USER.put("10.210.10.54", "测试环境-54-DEFAULT");
        TEST_ENVIRONMENT_IP_USER.put("10.210.10.60", "测试环境-60");
        TEST_ENVIRONMENT_IP_USER.put("10.210.10.63", "测试环境-63");
        TEST_ENVIRONMENT_IP_USER.put("10.210.10.85", "测试环境-85");

        TEST_ENVIRONMENT_IPS = ListUtil.newArrayList(TEST_ENVIRONMENT_IP_USER.keySet());

        // 开发IP用户
        DEVELOPER_LOCAL_ENVIRONMENT_IP_USER.put("10.210.13.13", "黄泽源");
        DEVELOPER_LOCAL_ENVIRONMENT_IP_USER.put("10.210.12.12", "黄泽源");
        DEVELOPER_LOCAL_ENVIRONMENT_IP_USER.put("10.210.12.49", "王德椿");

        DEVELOPER_LOCAL_ENVIRONMENT_IPS = ListUtil.newArrayList(DEVELOPER_LOCAL_ENVIRONMENT_IP_USER.keySet());
    }

}
