package com.kkinikong.be.batch.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.kkinikong.be.batch.dto.StoreCsv;

@Configuration
public class StoreCsvReader {

  @Bean
  @StepScope
  public FlatFileItemReader<StoreCsv> csvStoreReader(
      @Value("#{jobParameters['csvFile']}") String csvFile) {

    return new FlatFileItemReaderBuilder<StoreCsv>()
        .name("storeCsvReader")
        .resource(new FileSystemResource(csvFile))
        .linesToSkip(1) // 첫 줄 헤더 건너 뛰기
        .delimited()
        .names("name", "address", "latitude", "longitude", "updatedDate", "category")
        .fieldSetMapper(
            new BeanWrapperFieldSetMapper<>() {
              {
                setTargetType(StoreCsv.class);
              }
            })
        .build();
  }
}
