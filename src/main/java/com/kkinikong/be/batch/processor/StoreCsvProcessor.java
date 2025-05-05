package com.kkinikong.be.batch.processor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.kkinikong.be.batch.dto.StoreCsv;
import com.kkinikong.be.batch.exception.BatchException;
import com.kkinikong.be.batch.exception.errorcode.BatchErrorCode;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;

@Component
public class StoreCsvProcessor implements ItemProcessor<StoreCsv, Store> {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public Store process(StoreCsv item) throws Exception {
    return Store.builder()
        .name(item.getName())
        .region(extractRegion(item.getAddress()))
        .category(Category.fromLabel(CategoryMapper.mapToUpperCategory(item.getCategory())))
        .address(item.getAddress())
        .latitude(Double.parseDouble(item.getLatitude()))
        .longitude(Double.parseDouble(item.getLongitude()))
        .updatedDate(LocalDate.parse(item.getUpdatedDate(), DATE_FORMATTER))
        .build();
  }

  private String extractRegion(String address) {
    String[] parts = address.split(" ");
    if (parts.length < 2) {
      throw new BatchException(BatchErrorCode.INVALID_ADDRESS_FORMAT);
    }
    return parts[0] + " " + parts[1]; // 시 + 구
  }
}
