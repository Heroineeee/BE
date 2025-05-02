package com.kkinikong.be.batch.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.kkinikong.be.batch.dto.StoreCsv;
import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;

public class StoreCsvProcessorTest {
  private final StoreCsvProcessor processor = new StoreCsvProcessor();

  @Test
  void process_convert_success() throws Exception {
    // given
    StoreCsv csv = new StoreCsv();
    csv.setName("테스트가게");
    csv.setAddress("인천광역시 서구 장고개로 100");
    csv.setLatitude("37.123456");
    csv.setLongitude("126.654321");
    csv.setUpdatedDate("2024-11-14");
    csv.setCategory("한식");

    // when
    Store result = processor.process(csv);

    // then
    assertEquals("테스트가게", result.getName());
    assertEquals("인천광역시 서구", result.getRegion());
    assertEquals("인천광역시 서구 장고개로 100", result.getAddress());
    assertEquals(37.123456, result.getLatitude());
    assertEquals(126.654321, result.getLongitude());
    assertEquals(LocalDate.of(2024, 11, 14), result.getUpdatedDate());
    assertEquals(Category.KOREAN, result.getCategory());
  }
}
