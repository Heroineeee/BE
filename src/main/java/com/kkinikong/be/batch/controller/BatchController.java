package com.kkinikong.be.batch.controller;

import org.springframework.batch.core.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.batch.service.BatchService;
import com.kkinikong.be.global.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/batch")
@Tag(name = "Batch", description = "Batch 관련 API")
@RequiredArgsConstructor
public class BatchController {

  private final BatchService batchService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "csv파일 db에 넣기")
  public ResponseEntity<ApiResponse<Object>> uploadAndRunJob(
      @RequestPart("file") MultipartFile file) {

    try {
      String result = batchService.saveAndRunCsvJob(file);
      return ResponseEntity.ok(ApiResponse.from(result));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(ApiResponse.from(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.from("처리 중 오류 발생: " + e.getMessage()));
    }
  }
}
