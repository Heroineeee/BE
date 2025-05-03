package com.kkinikong.be.batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.FileSystemResource;

import org.junit.jupiter.api.Test;

import com.kkinikong.be.batch.dto.StoreCsv;

public class StoreCsvReaderTest {

  @Test
  void readAllCsvLines() throws Exception {
    FlatFileItemReader<StoreCsv> reader =
        new FlatFileItemReaderBuilder<StoreCsv>()
            .name("storeCsvReader")
            .resource(new FileSystemResource("uploads/인천광역시_서구.csv"))
            .delimited()
            .names("name", "address", "latitude", "longitude", "updatedDate", "category")
            .fieldSetMapper(
                new BeanWrapperFieldSetMapper<>() {
                  {
                    setTargetType(StoreCsv.class);
                  }
                })
            .build();

    reader.open(new ExecutionContext());
    StoreCsv item;
    while ((item = reader.read()) != null) {
      System.out.println(item);
    }
    reader.close();
  }
}
