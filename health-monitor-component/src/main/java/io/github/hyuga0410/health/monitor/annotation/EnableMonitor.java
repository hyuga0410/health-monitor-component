package io.github.hyuga0410.health.monitor.annotation;

import io.github.hyuga0410.health.monitor.MonitorConfiguration;
import io.github.hyuga0410.health.monitor.core.AutomaticHeartbeatMechanism;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 监控开启注解
 *
 * @author hyuga
 * @since 2021-12-01 10:38:12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MonitorConfiguration.class, AutomaticHeartbeatMechanism.class})
@Documented
@Inherited
public @interface EnableMonitor {

    /**
     * 项目类别
     */
    String project();

}
