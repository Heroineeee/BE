package com.kkinikong.be.batch.writer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.repository.StoreRepository;

@SpringBootTest
@Import({StoreCsvWriter.class})
public class StoreCsvWriterTest {

  @Autowired private JpaItemWriter<Store> storeWriter;

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired private StoreRepository storeRepository;

  @Test
  @Transactional
  void writer_check() throws Exception {
    // given
    Store store =
        Store.builder()
            .name("테스트가게")
            .region("인천광역시 서구")
            .category(Category.KOREAN)
            .address("인천 서구 청마로 123")
            .latitude(37.12345)
            .longitude(126.54321)
            .updatedDate(LocalDate.of(2024, 11, 14))
            .build();

    storeWriter.afterPropertiesSet();
    storeWriter.write(new Chunk<>(List.of(store)));

    // when
    List<Store> result = storeRepository.findAll();

    // then
    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getName()).isEqualTo("테스트가게");
  }
}
