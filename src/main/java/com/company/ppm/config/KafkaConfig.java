package com.company.ppm.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {

    @Bean
    public NewTopic taskEventsTopic(AppProperties appProperties) {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getTaskEvents())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kpiReadingsTopic(AppProperties appProperties) {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getKpiReadings())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kpiAggregatesTopic(AppProperties appProperties) {
        return TopicBuilder.name(appProperties.getKafka().getTopics().getKpiAggregates())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean(name = "defaultKafkaStreamsConfig")
    public KafkaStreamsConfiguration kafkaStreamsConfiguration(org.springframework.core.env.Environment environment) {
        Map<String, Object> props = new HashMap<>();
        props.put(
                StreamsConfig.APPLICATION_ID_CONFIG,
                environment.getProperty("spring.kafka.streams.application-id", "ppm-kpi-aggregator")
        );
        props.put(
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.bootstrap-servers", "localhost:9092")
        );
        return new KafkaStreamsConfiguration(props);
    }
}
