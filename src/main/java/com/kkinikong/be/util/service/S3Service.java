package com.kkinikong.be.util.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.util.exception.S3Exception;
import com.kkinikong.be.util.exception.errorcode.S3ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

  private final AmazonS3 s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String uploadFile(MultipartFile file) {
    try {
      String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
      s3Client.putObject(bucket, fileName, file.getInputStream(), null);
      return s3Client.getUrl(bucket, fileName).toString();
    } catch (AmazonServiceException | IOException e) {
      throw new S3Exception(S3ErrorCode.S3_UPLOAD_FAIL);
    }
  }
}
