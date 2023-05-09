package io.github.hyuga0410.health.monitor.constants;

/**
 * 服务心跳KEY
 *
 * @author hyuga
 * @since 2021-12-09 15:21:33
 */
public interface ServiceHeartbeatKey {

    String SERVICE_UP_QUEUE = "monitor_service_up";
    String SERVICE_HEARTBEAT_QUEUE = "monitor_service_heartbeat";
    String SERVICE_DOWN_QUEUE = "monitor_service_down";

}
