package io.github.hyuga0410.health.monitor;

import io.github.hyuga0410.health.monitor.annotation.EnableMonitor;
import io.github.hyuga0410.health.monitor.constants.MonitorConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * MonitorConfiguration
 *
 * @author hyuga
 * @since 2022/01/12
 */
@Component
@Slf4j
public class MonitorConfiguration implements ImportBeanDefinitionRegistrar {

    private static String PROJECT;

    public static String project() {
        return PROJECT;
    }

    /**
     * 根据导入{@code @Configuration}类的给定注释元数据，根据需要注册bean定义
     *
     * @param importingClassMetadata 导入类的注释元数据
     * @param registry               当前bean定义注册表
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        Map<String, Object> defaultAttrs = importingClassMetadata.getAnnotationAttributes(EnableMonitor.class.getName());
        if (defaultAttrs == null) {
            log.info("Monitor Initialization failed ~~~");
            return;
        }

        PROJECT = (String) defaultAttrs.get(MonitorConstant.PROJECT);

        log.info("Monitor Successful initialization ~~~");
    }

}