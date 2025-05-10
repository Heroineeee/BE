package com.kkinikong.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

  @Bean
  public AmazonS3 amazonS3(
      @Value("${cloud.aws.credentials.accessKey}") String accessKey,
      @Value("${cloud.aws.credentials.secretKey}") String secretKey,
      @Value("${cloud.aws.region.static}") String region) {

    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonS3ClientBuilder.standard()
        .withRegion(Regions.fromName(region))
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build();
  }
}
