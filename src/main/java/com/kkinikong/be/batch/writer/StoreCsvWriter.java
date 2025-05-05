package com.kkinikong.be.batch.writer;

import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;

import com.kkinikong.be.store.domain.Store;

@Configuration
public class StoreCsvWriter {

  @Bean
  public JpaItemWriter<Store> storeJpaItemWriter(EntityManagerFactory entityManagerFactory) {
    return new JpaItemWriterBuilder<Store>()
        .entityManagerFactory(entityManagerFactory)
        .usePersist(false)
        .build();
  }
}
