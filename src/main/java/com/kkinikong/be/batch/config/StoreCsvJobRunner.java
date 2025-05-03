package com.kkinikong.be.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StoreCsvJobRunner {

  private final JobLauncher jobLauncher;
  private final Job storeCsvJob;
  private final JobRepository jobRepository;

  @Bean
  public CommandLineRunner runStoreCsvJobOnce() {
    return args -> {
      JobParameters jobParameters = new JobParameters();
      if (jobRepository.getLastJobExecution(storeCsvJob.getName(), jobParameters) == null) {
        jobLauncher.run(
            storeCsvJob,
            new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters());
        System.out.println("csv 파일 db에 넣기 완료");
      } else {
        System.out.println("이미 실행 되었으므로 다시 실행하지 않음");
      }
    };
  }
}
