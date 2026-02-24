package com.company.ppm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableCaching
@EnableAsync
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {
}
