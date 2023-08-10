package com.kfang.service.agent.monitor.config;

import kfang.infra.service.config.MapperScannerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AutoMapConfiguration
 *
 * @author hyuga
 */
@Configuration
public class AutoMapConfiguration {

    private static final String AUTO_MAPPER_SCANNER = "autoMapperScanner";
    private static final String BASE_PACKAGE = "com.kfang.service.agent.monitor.dao.automapper";

    @Bean(name = AUTO_MAPPER_SCANNER)
    public MapperScannerConfig autoMapperScanner() {
        MapperScannerConfig orgMapperScanner = new MapperScannerConfig();
        orgMapperScanner.setBasePackage(BASE_PACKAGE);
        return orgMapperScanner;
    }

}
