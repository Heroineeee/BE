package com.kkinikong.be.batch.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.batch.util.FileUtil;
import com.kkinikong.be.global.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/batch")
@Tag(name = "Batch", description = "Batch 관련 API")
@RequiredArgsConstructor
public class StoreCsvBatchController {

  private final JobLauncher jobLauncher;
  private final Job storeCsvJob;
  private final JobRepository jobRepository;

  @Value("${csv.upload-dir}")
  private String uploadDir;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "csv파일 db에 넣기", description = "")
  public ResponseEntity<ApiResponse<Object>> uploadAndRunJob(
      @Parameter(
              description = "CSV 파일",
              content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
          @RequestPart("file")
          MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.from("파일이 비어있습니다."));
    }

    try {
      // 원본 파일명
      String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
      Path destinationPath = Paths.get(uploadDir).resolve(originalFilename).normalize();
      File destFile = destinationPath.toFile();

      // 디렉토리 없으면 생성
      destFile.getParentFile().mkdirs();
      file.transferTo(destFile);

      // 해시값 생성
      String fileHash = FileUtil.getFileHash(destFile.getAbsolutePath());

      JobParameters jobParameters =
          new JobParametersBuilder()
              .addString("fileHash", fileHash)
              .addString("csvFile", destFile.getAbsolutePath())
              .toJobParameters();

      JobExecution lastExecution =
          jobRepository.getLastJobExecution(storeCsvJob.getName(), jobParameters);

      if (lastExecution != null && lastExecution.getStatus() == BatchStatus.COMPLETED) {
        return ResponseEntity.ok(ApiResponse.from("이미 처리된 파일입니다. 중복 실행하지 않습니다."));
      }

      JobExecution execution = jobLauncher.run(storeCsvJob, jobParameters);
      if (execution.getStatus() == BatchStatus.COMPLETED) {
        return ResponseEntity.ok(ApiResponse.from("CSV 파일 DB 저장 성공"));
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.from("CSV 처리 중 오류 발생: " + execution.getStatus()));
      }

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.from("파일 저장 실패: " + e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.from("CSV 처리 실패: " + e.getMessage()));
    }
  }
}
