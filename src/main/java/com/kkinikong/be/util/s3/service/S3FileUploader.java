package com.kkinikong.be.util.s3.service;

import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kkinikong.be.util.s3.exception.S3Exception;
import com.kkinikong.be.util.s3.exception.errorcode.S3ErrorCode;

public interface S3FileUploader {
  List<String> ALLOWED_CONTENT_TYPES =
      List.of(
          "image/jpeg", "image/png", "image/heic" // .jpg, .jpeg, .png, .heic
          );
  List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "heic");

  String uploadFile(MultipartFile file);

  void deleteFile(String imageUrl);

  String extractFileKeyFromUrl(String imageUrl);

  // 공통 유틸 : 파일 포맷 검증
  default void validateFileFormat(MultipartFile file) {
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
