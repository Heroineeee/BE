package com.kkinikong.be.util.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3FileUploader {
  void validateFileFormat(MultipartFile file);

  String uploadFile(MultipartFile file);
}
