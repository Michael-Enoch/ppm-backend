package com.company.ppm.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final SecurityProperties security = new SecurityProperties();
    private final IngestionProperties ingestion = new IngestionProperties();
    private final KafkaProperties kafka = new KafkaProperties();
    private final StorageProperties storage = new StorageProperties();

    @Getter
    @Setter
    public static class SecurityProperties {
        private final JwtProperties jwt = new JwtProperties();
    }

    @Getter
    @Setter
    public static class JwtProperties {
        @NotBlank
        private String issuer;

        @NotBlank
        private String secret;

        @Min(1)
        private long accessTokenMinutes = 30;

        @Min(1)
        private long refreshTokenDays = 14;
    }

    @Getter
    @Setter
    public static class IngestionProperties {
        @Min(1)
        private int rateLimitPerMinute = 60;
    }

    @Getter
    @Setter
    public static class KafkaProperties {
        private final Topics topics = new Topics();

        @Getter
        @Setter
        public static class Topics {
            @NotBlank
            private String taskEvents;

            @NotBlank
            private String kpiReadings;

            @NotBlank
            private String kpiAggregates;
        }
    }

    @Getter
    @Setter
    public static class StorageProperties {
        @NotBlank
        private String endpoint;

        @NotBlank
        private String accessKey;

        @NotBlank
        private String secretKey;

        @NotBlank
        private String bucket;

        @Min(1)
        private int presignedUrlMinutes = 60;
    }
}
