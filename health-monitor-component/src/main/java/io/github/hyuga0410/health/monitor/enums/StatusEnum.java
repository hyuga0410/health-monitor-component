package io.github.hyuga0410.health.monitor.enums;

import io.github.hyuga0410.health.monitor.model.ServiceLogModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务状态枚举
 *
 * @author hyuga
 * @since 2021-11-26 11:30:54
 */
@AllArgsConstructor
public enum StatusEnum {

    /**
     *
     */
    ONLINE("在线"),
    OFFLINE("离线"),
    ;

    @Getter
    private final String desc;

    @SuppressWarnings("unused")
    public static boolean isOnline(ServiceLogModel model) {
        if (model == null) {
            return false;
        }
        return isOnline(model.getStatus());
    }

    public static boolean isOnline(StatusEnum status) {
        if (status == null) {
            return false;
        }
        return ONLINE == status;
    }

    @SuppressWarnings("unused")
    public static boolean isOffline(ServiceLogModel model) {
        if (model == null) {
            return false;
        }
        return isOffline(model.getStatus());
    }

    public static boolean isOffline(StatusEnum status) {
        if (status == null) {
            return false;
        }
        return OFFLINE == status;
    }

}
