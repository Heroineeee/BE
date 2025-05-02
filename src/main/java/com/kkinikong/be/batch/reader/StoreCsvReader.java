package com.kkinikong.be.batch.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.kkinikong.be.batch.dto.StoreCsv;

@Configuration
public class StoreCsvReader {

  @Bean
  public FlatFileItemReader<StoreCsv> csvStoreReader() {
    return new FlatFileItemReaderBuilder<StoreCsv>()
        .name("storeCsvReader")
        .resource(new ClassPathResource("data/인천광역시_서구.csv")) // 하드 코딩 (확장 예정)
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
