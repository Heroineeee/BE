package com.kkinikong.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@EnableElasticsearchRepositories(
    basePackages = "com.kkinikong.be.community.repository.elasticsearch")
@SpringBootApplication
public class KkinikongBeApplication {

  public static void main(String[] args) {
    SpringApplication.run(KkinikongBeApplication.class, args);
  }
}
