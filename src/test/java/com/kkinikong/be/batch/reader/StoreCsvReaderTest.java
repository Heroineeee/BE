package com.kkinikong.be.batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.Test;

import com.kkinikong.be.batch.dto.StoreCsv;

@SpringBootTest
@Import(StoreCsvReader.class)
public class StoreCsvReaderTest {

  @Autowired private FlatFileItemReader<StoreCsv> csvStoreReader;

  @Test
  void readAllCsvLines() throws Exception {
    csvStoreReader.open(new ExecutionContext());
    StoreCsv storeCsv;
    while ((storeCsv = csvStoreReader.read()) != null) {
      System.out.println(storeCsv);
    }
    csvStoreReader.close();
  }
}
