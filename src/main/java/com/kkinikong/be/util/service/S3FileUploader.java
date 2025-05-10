package com.kkinikong.be.util.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3FileUploader {
  String uploadFile(MultipartFile file);
}
