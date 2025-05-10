package com.kkinikong.be.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.store.dto.response.StoreUploadResponse;
import com.kkinikong.be.store.service.StoreUploadService;

@RestController
@RequestMapping("/api/v1/store/upload")
@RequiredArgsConstructor
@Tag(name = "StoreUpload", description = "CSV 파일 업로드를 통한 가맹점 등록 API (관리자 전용, 프론트 연동 X)")
public class StoreUploadController {

  private final StoreUploadService storeUploadService;

  @Operation(summary = "가맹점 CSV 업로드", description = "CSV파일을 업로드하여 가맹점을 등록한다.")
  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<ApiResponse<Object>> uploadStoreCsv(
      @RequestPart("file") MultipartFile file) {
    StoreUploadResponse response = storeUploadService.upload(file);
    return ResponseEntity.ok(
        ApiResponse.from(
            String.format(
                "CSV 파일 업로드 완료: 총 %d건 중 %d건 저장됨", response.totalCount(), response.saveCount())));
  }
}
