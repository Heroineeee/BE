package com.kkinikong.be.batch.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.batch.util.FileUtil;

@Service
@RequiredArgsConstructor
public class BatchService {

  private final JobLauncher jobLauncher;
  private final Job storeCsvJob;
  private final JobRepository jobRepository;

  @Value("${csv.upload-dir}")
  private String uploadDir;

  public String saveAndRunCsvJob(MultipartFile file) throws Exception {
    File savedFile = saveUploadedFile(file);
    String fileHash = generateFileHash(savedFile);
    JobParameters jobParameters = createJobParameters(fileHash, savedFile);

    if (isDuplicateExecution(jobParameters)) {
      return "이미 처리된 파일입니다. 중복 실행하지 않습니다.";
    }

    runBatchJob(jobParameters);
    return "CSV 파일 DB 저장 성공";
  }

  private File saveUploadedFile(MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("파일이 비어있습니다.");
    }

    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
    Path destinationPath = Paths.get(uploadDir).resolve(originalFilename).normalize();
    File destFile = destinationPath.toFile();
    destFile.getParentFile().mkdirs();
    file.transferTo(destFile);
    return destFile;
  }

  private String generateFileHash(File file) {
    return FileUtil.getFileHash(file.getAbsolutePath());
  }

  private JobParameters createJobParameters(String fileHash, File file) {
    return new JobParametersBuilder()
        .addString("fileHash", fileHash)
        .addString("csvFile", file.getAbsolutePath())
        .toJobParameters();
  }

  private boolean isDuplicateExecution(JobParameters params) {
    JobExecution lastExecution = jobRepository.getLastJobExecution(storeCsvJob.getName(), params);
    return lastExecution != null && lastExecution.getStatus() == BatchStatus.COMPLETED;
  }

  private void runBatchJob(JobParameters params) throws Exception {
    JobExecution execution = jobLauncher.run(storeCsvJob, params);
    if (execution.getStatus() != BatchStatus.COMPLETED) {
      throw new IllegalStateException("CSV 처리 중 오류 발생: " + execution.getStatus());
    }
  }
}
