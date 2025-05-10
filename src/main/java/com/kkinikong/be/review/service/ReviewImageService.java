package com.kkinikong.be.review.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.util.exception.S3Exception;
import com.kkinikong.be.util.exception.errorcode.S3ErrorCode;
import com.kkinikong.be.util.service.S3FileUploader;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewImageService implements S3FileUploader {

  private final AmazonS3 s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private static final List<String> ALLOWED_CONTENT_TYPES =
      List.of(
          "image/jpeg", "image/png", "image/heic" // .jpg, .jpeg, .png, .heic
          );
  private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "heic");

  @Override
  public String uploadFile(MultipartFile file) {
    validateFileFormat(file);

    try {
      String datePrefix = LocalDate.now().toString();
      String uuid = UUID.randomUUID().toString();
      String fileName = "review/" + datePrefix + "/" + uuid + "_" + file.getOriginalFilename();

      s3Client.putObject(bucket, fileName, file.getInputStream(), null);
      return s3Client.getUrl(bucket, fileName).toString();
    } catch (AmazonServiceException | IOException e) {
      throw new S3Exception(S3ErrorCode.S3_UPLOAD_FAIL);
    }
  }

  @Override
  public void validateFileFormat(MultipartFile file) {
    String contentType = file.getContentType();
    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

    if (contentType == null || extension == null) {
      throw new S3Exception(S3ErrorCode.FILE_FORMAT_NOT_SUPPORTED);
    }

    extension = extension.toLowerCase();

    if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())
        || !ALLOWED_EXTENSIONS.contains(extension)) {
      throw new S3Exception(S3ErrorCode.FILE_FORMAT_NOT_SUPPORTED);
    }
  }
}
