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

import com.kkinikong.be.batch.exception.BatchException;
import com.kkinikong.be.batch.exception.errorcode.BatchErrorCode;
import com.kkinikong.be.batch.util.FileUtil;

@Service
@RequiredArgsConstructor
public class BatchService {

  private final JobLauncher jobLauncher;
  private final Job storeCsvJob;
  private final JobRepository jobRepository;

  @Value("${csv.upload-dir}")
  private String uploadDir;

  /// CSV 파일을 저장하고 배치 Job 실행
  public String saveAndRunCsvJob(MultipartFile file) {
    File savedFile = saveUploadedFile(file);
    String fileHash = generateFileHash(savedFile);
    JobParameters jobParameters = createJobParameters(fileHash, savedFile);

    if (isDuplicateExecution(jobParameters)) {
      return "이미 처리된 파일입니다. 중복 실행하지 않습니다.";
    }

    runBatchJob(jobParameters);
    return "CSV 파일 DB 저장 성공";
  }

  ///  업로드 된 파일을 서버의 지정 디렉토리에 저장
  private File saveUploadedFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new BatchException(BatchErrorCode.EMPTY_FILE);
    }

    try {
      String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
      Path destinationPath = Paths.get(uploadDir).resolve(originalFilename).normalize();
      File destFile = destinationPath.toFile();
      destFile.getParentFile().mkdirs();
      file.transferTo(destFile);
      return destFile;
    } catch (IOException e) {
      throw new BatchException(BatchErrorCode.FILE_SAVE_FAILED);
    }
  }

  ///  지정된 파일의 SHA-256 해시값 생성
  private String generateFileHash(File file) {
    String hash = FileUtil.getFileHash(file.getAbsolutePath());
    if (hash == null) {
      throw new BatchException(BatchErrorCode.FILE_HASH_FAILED);
    }
    return hash;
  }

  ///  배치 Job 실행에 필요한 JobParameters 생성
  private JobParameters createJobParameters(String fileHash, File file) {
    return new JobParametersBuilder()
        .addString("fileHash", fileHash)
        .addString("csvFile", file.getAbsolutePath())
        .toJobParameters();
  }

  ///  동일한 JobParameters로 이미 실행된 Job이 있는지 확인
  private boolean isDuplicateExecution(JobParameters params) {
    JobExecution lastExecution = jobRepository.getLastJobExecution(storeCsvJob.getName(), params);
    return lastExecution != null && lastExecution.getStatus() == BatchStatus.COMPLETED;
  }

  ///  JobLauncher 를 통해 실제 배치 Job 실행
  private void runBatchJob(JobParameters params) {
    try {
      JobExecution execution = jobLauncher.run(storeCsvJob, params);
      if (execution.getStatus() != BatchStatus.COMPLETED) {
        throw new BatchException(BatchErrorCode.JOB_EXECUTION_FAILED);
      }
    } catch (Exception e) {
      throw new BatchException(BatchErrorCode.JOB_EXECUTION_FAILED);
    }
  }
}
