package com.company.ppm.storage;

import com.company.ppm.config.AppProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ObjectStorageService {

    private static final Logger log = LoggerFactory.getLogger(ObjectStorageService.class);

    private final AppProperties appProperties;
    private MinioClient minioClient;
    private boolean storageReady;

    public ObjectStorageService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(appProperties.getStorage().getEndpoint())
                .credentials(appProperties.getStorage().getAccessKey(), appProperties.getStorage().getSecretKey())
                .build();

        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(appProperties.getStorage().getBucket()).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(appProperties.getStorage().getBucket()).build());
            }
            storageReady = true;
        } catch (Exception ex) {
            storageReady = false;
            log.warn("Object storage is unavailable at startup: {}", ex.getMessage());
        }
    }

    public void upload(String objectKey, byte[] bytes, String contentType) {
        ensureStorageReady();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(appProperties.getStorage().getBucket())
                            .object(objectKey)
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to upload report", ex);
        }
    }

    public String generateDownloadUrl(String objectKey) {
        ensureStorageReady();
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(appProperties.getStorage().getBucket())
                            .object(objectKey)
                            .expiry(appProperties.getStorage().getPresignedUrlMinutes() * 60)
                            .build()
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate download URL", ex);
        }
    }

    private void ensureStorageReady() {
        if (!storageReady) {
            throw new IllegalStateException("Object storage is unavailable");
        }
    }
}
