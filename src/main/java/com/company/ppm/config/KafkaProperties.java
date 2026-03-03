// src/main/java/com/company/ppm/config/KafkaProperties.java
package com.company.ppm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    @Setter
    private String bootstrapServers;
    private final Streams streams = new Streams();
    private final Topics topics = new Topics();

    @Setter
    @Getter
    public static class Streams {
        private String applicationId;

    }

    @Setter
    @Getter
    public static class Topics {
        private String taskEvents;
        private String kpiReadings;
        private String kpiAggregates;

    }
}