package com.company.ppm.events;

import com.company.ppm.config.AppProperties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class KpiStreamTopology {

    @Bean
    public KStream<String, KpiReadingEvent> kpiAggregationStream(StreamsBuilder builder, AppProperties appProperties) {
        JsonSerde<KpiReadingEvent> readingSerde = new JsonSerde<>(KpiReadingEvent.class);
        JsonSerde<KpiAggregateEvent> aggregateSerde = new JsonSerde<>(KpiAggregateEvent.class);

        KStream<String, KpiReadingEvent> source = builder.stream(
                appProperties.getKafka().getTopics().getKpiReadings(),
                Consumed.with(Serdes.String(), readingSerde)
        );

        KTable<String, KpiAggregateEvent> aggregateTable = source
                .groupByKey(Grouped.with(Serdes.String(), readingSerde))
                .aggregate(
                        KpiAggregateEvent::new,
                        (key, value, aggregate) -> {
                            aggregate.apply(value);
                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), aggregateSerde)
                );

        aggregateTable
                .toStream()
                .to(appProperties.getKafka().getTopics().getKpiAggregates(), Produced.with(Serdes.String(), aggregateSerde));

        return source;
    }
}
