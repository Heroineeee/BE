package com.kkinikong.be.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.kkinikong.be.batch.dto.StoreCsv;
import com.kkinikong.be.batch.processor.StoreCsvProcessor;
import com.kkinikong.be.store.domain.Store;

@Configuration
public class StoreCsvJobConfig {

  @Bean
  public Job storeCsvJob(JobRepository jobRepository, Step storeCsvStep) {
    return new JobBuilder("storeCsvJob", jobRepository).start(storeCsvStep).build();
  }

  @Bean
  public Step storeCsvStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<StoreCsv> csvStoreReader,
      StoreCsvProcessor processor,
      JpaItemWriter<Store> csvStoreWriter) {
    return new StepBuilder("storeCsvStep", jobRepository)
        .<StoreCsv, Store>chunk(100, transactionManager)
        .reader(csvStoreReader)
        .processor(processor)
        .writer(csvStoreWriter)
        .build();
  }
}
