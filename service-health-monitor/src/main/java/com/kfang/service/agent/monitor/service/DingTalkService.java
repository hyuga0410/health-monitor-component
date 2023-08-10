// package com.kfang.service.agent.monitor.service;
//
// import cn.hyugatool.core.object.ObjectUtil;
// import cn.hyugatool.core.string.StringUtil;
// import cn.hyugatool.system.NetworkUtil;
// import kfang.agent.feature.dingtalk.message.CoreParameter;
// import kfang.agent.feature.dingtalk.message.MessageMarkdown;
// import kfang.agent.feature.dingtalk.util.DingTalkUtil;
// import kfang.agent.feature.monitor.enums.StatusEnum;
// import kfang.agent.feature.saas.constants.SaasConstants;
// import kfang.infra.common.KfangInfraCommonProperties;
// import org.springframework.core.env.Environment;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.util.Properties;
//
// /**
//  * 微信服务
//  *
//  * @author hyuga
//  * @since 2021-12-13 09:20:41
//  */
// @Service
// public class DingTalkService {
//
//     @Resource
//     private Environment environment;
//     @Resource
//     private KfangInfraCommonProperties kfangInfraCommonProperties;
//
//     private static final String SECRET = "secret";
//     private static final String ACCESS_TOKEN = "accessToken";
//
//     private static final String NOTIFY_TEMPLATE = "# %s - 服务 - %s\n- 系统 : %s\n- 地址 : %s\n- 端口 : %s\n- 时间 : %s\n- %s\n%s";
//
//     /**
//      * SpringBoot端口配置KEY
//      */
//     private static final String SERVER_PORT = "server.port";
//
//     public void sendUpOrDown(String env, String ip, Integer port, String name, String timestamp, StatusEnum status, String duration) throws Exception {
//         String statusDesc = ObjectUtil.equals(StatusEnum.ONLINE, status) ? "上线" : "离线";
//         String durationStr = StringUtil.EMPTY;
//         if (StringUtil.hasText(duration)) {
//             if (StatusEnum.isOnline(status)) {
//                 durationStr += "距上次服务离线已经过了: " + duration;
//             }
//             if (StatusEnum.isOffline(status)) {
//                 durationStr += "本次服务总持续时长: " + duration;
//             }
//         }
//
//         String title = String.format("%s - 服务 - %s", env.toUpperCase(), statusDesc);
//         String text = String.format(NOTIFY_TEMPLATE, env.toUpperCase(), statusDesc, name, ip, port, timestamp, durationStr, getMonitorViewUrl(env));
//
//         sendDingTalk(title, text);
//     }
//
//     public void sendDingTalk(String title, String text) throws Exception {
//         Properties dingTalkFileProperties = getDingTalkProperties();
//
//         CoreParameter coreParameter = CoreParameter.builder()
//                 .accessToken(dingTalkFileProperties.getProperty(ACCESS_TOKEN))
//                 .secret(dingTalkFileProperties.getProperty(SECRET))
//                 .build();
//         MessageMarkdown messageMarkdown = new MessageMarkdown(coreParameter);
//
//         messageMarkdown.setTitle(title);
//         messageMarkdown.setText(text);
//         DingTalkUtil.sendMessage(messageMarkdown);
//     }
//
//     public String getMonitorViewUrl(String env) {
//         return String.format("http://%s:%s/monitor/server/view?env=%s", NetworkUtil.getLocalIpAddr(), environment.getProperty(SERVER_PORT), env.toLowerCase());
//     }
//
//     private Properties getDingTalkProperties() {
//         Properties properties = new Properties();
//
//         boolean isDev = StringUtil.equalsIgnoreCase(SaasConstants.DEV, kfangInfraCommonProperties.getEnv().getDeploy());
//         if (isDev) {
//             properties.setProperty(ACCESS_TOKEN, "446cfbe11456505cf01ac32f26bf88748395b1761121d991105154134244e35b");
//             properties.setProperty(SECRET, "SECc4c82c1fc0fc9e171682236c330c3245553bc18f887d1a215daf3aae1b6c479a");
//         } else {
//             properties.setProperty(ACCESS_TOKEN, "0be54bbc310a7514a8688a5bb41c64e282ce9c9c145633d4e6c71f6dbc466709");
//             properties.setProperty(SECRET, "SECe0b10fe50adc97e876a5c6f0f5a3d158b311b320bb8888c37e7ff3d086099aac");
//         }
//         return properties;
//     }
//
// }
