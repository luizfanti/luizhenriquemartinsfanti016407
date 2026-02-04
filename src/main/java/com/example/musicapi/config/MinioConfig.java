package com.example.musicapi.config;

import com.example.musicapi.config.properties.MinioProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Bean
    public S3Client s3Client(MinioProperties props) {
        AwsBasicCredentials creds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(MinioProperties props) {
        AwsBasicCredentials creds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());

        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.US_EAST_1)
                .build();
    }
}
