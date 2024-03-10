package com.mvc.coinsimulation.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Amazon S3 클라이언트를 구성하는 클래스
 *
 * @Author 이상현
 * @Version 1.0.0
 * @See None
 */
@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * Amazon S3 클라이언트 빈을 생성하여 반환하는 메서드
     *
     * @return AmazonS3Client
     */
    @Bean
    public AmazonS3Client amazonS3Client() {
        // AWS 자격 증명 생성
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        // Amazon S3 클라이언트 빌더를 사용하여 클라이언트 생성
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region) // 리전 설정
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)) // 자격 증명 설정
                .build();
    }
}
