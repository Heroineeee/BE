package com.kkinikong.be.image.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.image.exception.S3Exception;
import com.kkinikong.be.image.exception.errorcode.S3ErrorCode;
import com.kkinikong.be.image.type.S3Bucket;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

  private final AmazonS3 s3Client;

  private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "heic");
  private static final List<String> ALLOWED_CONTENT_TYPES =
      List.of("image/jpeg", "image/png", "image/heic");

  public String uploadSingleFile(MultipartFile file, S3Bucket s3Bucket) {
    return uploadFileToS3(file, s3Bucket);
  }

  public List<String> uploadFileList(List<MultipartFile> files, S3Bucket s3Bucket) {
    List<String> imageUrls = new ArrayList<>();
    for (MultipartFile file : files) {
      imageUrls.add(uploadFileToS3(file, s3Bucket));
    }
    return imageUrls;
  }

  private String uploadFileToS3(MultipartFile file, S3Bucket s3Bucket) {
    validateFileFormat(file);

    try {
      String datePrefix = LocalDate.now().toString();
      String uuid = UUID.randomUUID().toString();
      String fileName = datePrefix + "/" + uuid + "_" + file.getOriginalFilename();

      s3Client.putObject(s3Bucket.getBucketName(), fileName, file.getInputStream(), null);
      return s3Client.getUrl(s3Bucket.getBucketName(), fileName).toString();
    } catch (AmazonServiceException | IOException e) {
      throw new S3Exception(S3ErrorCode.S3_UPLOAD_FAIL);
    }
  }

  public void deleteFile(String imageUrl, S3Bucket s3Bucket) {
    try {
      String fileKey = extractFileKeyFromUrl(imageUrl, s3Bucket);
      s3Client.deleteObject(s3Bucket.getBucketName(), fileKey);
    } catch (AmazonServiceException e) {
      throw new S3Exception(S3ErrorCode.S3_DELETE_FAIL);
    }
  }

  private String extractFileKeyFromUrl(String imageUrl, S3Bucket s3Bucket) {
    String baseUrl = s3Client.getUrl(s3Bucket.getBucketName(), "").toString();
    return imageUrl.replace(baseUrl, "");
  }

  // 파일 포맷 검증
  private void validateFileFormat(MultipartFile file) {
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
