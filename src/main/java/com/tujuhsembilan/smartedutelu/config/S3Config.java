package com.tujuhsembilan.smartedutelu.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.net.URI;

@Configuration
@Slf4j
public class S3Config {

    @Value("${application.minio.endpoint}")
    private String endpoint;

    @Value("${application.minio.access-key}")
    private String accessKey;

    @Value("${application.minio.secret-key}")
    private String secretKey;

    @Value("${application.minio.bucket}")
    private String bucket;

    @Bean
    public S3Client s3Client() {
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();

        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            log.info("MinIO bucket '{}' sudah ada", bucket);
        } catch (NoSuchBucketException e) {
            client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            log.info("MinIO bucket '{}' berhasil dibuat", bucket);
        } catch (Exception e) {
            log.warn("Tidak bisa memeriksa/membuat bucket '{}': {}", bucket, e.getMessage());
        }

        return client;
    }
}
