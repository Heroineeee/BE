package com.kkinikong.be.batch.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.batch.util.FileUtil;

@Configuration
@RequiredArgsConstructor
public class StoreCsvJobRunner {

  private final JobLauncher jobLauncher;
  private final Job storeCsvJob;
  private final JobRepository jobRepository;

  @Bean
  public CommandLineRunner runStoreCsvJobOnce() {
    return args -> {
      String uploadDir = "uploads/";

      File folder = new File(uploadDir);
      File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

      if (files != null) {
        for (File file : files) {
          String csvFilePath = file.getAbsolutePath();
          String fileHash = FileUtil.getFileHash(csvFilePath);

          JobParameters jobParameters =
              new JobParametersBuilder()
                  .addString("fileHash", fileHash)
                  .addString("csvFile", csvFilePath)
                  .toJobParameters();

          // 동일한 해시값으로 실행된 적이 있는지 확인
          if (jobRepository.getLastJobExecution(storeCsvJob.getName(), jobParameters) == null) {
            jobLauncher.run(storeCsvJob, jobParameters);
            System.out.println("CSV 파일 DB에 넣기 완료: " + csvFilePath);
          } else {
            System.out.println("이미 실행된 파일이므로 다시 실행하지 않음: " + csvFilePath);
          }
        }
      }
    };
  }
}
