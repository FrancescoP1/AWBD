package com.fmi.eduhub.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

  @Value("${aws.accessKey}")
  private String ACCESS_KEY;

  @Value("${aws.secretKey}")
  private String SECRET_KEY;

  @Bean
  public AWSCredentials awsCredentials() {
    return new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
  }

  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
        .withRegion(Regions.EU_CENTRAL_1)
        .build();
  }
}
