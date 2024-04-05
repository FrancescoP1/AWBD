package com.fmi.eduhub.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final AmazonS3 amazonS3;

  @Value("${aws.bucketName}")
  private String awsBucket;

  public String uploadFile(String key, MultipartFile file) {
    try {
      InputStream fileInputStream = file.getInputStream();
      ObjectMetadata fileMetaData = new ObjectMetadata();
      fileMetaData.setContentType(file.getContentType());
      amazonS3.putObject(new PutObjectRequest(awsBucket, key, fileInputStream, fileMetaData));
      return key;
    } catch(IOException ioException) {
      throw new RuntimeException(ioException);
    }
  }

  public String uploadFile(MultipartFile file) {
    String key = UUID.randomUUID().toString();
    return uploadFile(key, file);
  }

  public String generatePreSignedUrl(String key) {
    if(key == null || key.isEmpty()) {
      return null;
    }
    Date expiration = new Date();
    long expTimeMilliseconds = 5 * 60 * 1000 + expiration.getTime();
    expiration.setTime(expTimeMilliseconds);
    GeneratePresignedUrlRequest urlRequest =
        new GeneratePresignedUrlRequest(awsBucket, key)
            .withExpiration(expiration)
            .withMethod(HttpMethod.GET);
    return amazonS3.generatePresignedUrl(urlRequest).toString();
  }

  public String generatePreSignedDownloadUrl(String key) {
    if(key == null || key.isEmpty()) {
      return null;
    }
    Date expiration = new Date();
    long expTimeMilliseconds = 60 * 60 * 1000 + expiration.getTime();
    expiration.setTime(expTimeMilliseconds);

    ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides();
    headerOverrides.setContentDisposition("attachment");

    GeneratePresignedUrlRequest urlRequest =
        new GeneratePresignedUrlRequest(awsBucket, key)
            .withResponseHeaders(headerOverrides)
            .withExpiration(expiration)
            .withMethod(HttpMethod.GET);
    return amazonS3.generatePresignedUrl(urlRequest).toString();
  }

  public void deleteFile(String key) {
    if(key != null && !key.isEmpty()) {
      amazonS3.deleteObject(awsBucket, key);
    }
  }

  public void deleteFiles(List<String> keys) {
    DeleteObjectsRequest deleteObjectsRequest =
        new DeleteObjectsRequest(awsBucket)
            .withKeys(keys.toArray(new String[0]));
    amazonS3.deleteObjects(deleteObjectsRequest);
  }
}
