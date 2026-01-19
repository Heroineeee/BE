package com.kkinikong.be.global.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // 프로젝트 전체에서 @Async 어노테이션을 사용할 수 있도록 함
public class AsyncConfig implements AsyncConfigurer {

  @Bean(name = "notificationExecutor")
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5); // 기본 스레드 수
    executor.setMaxPoolSize(10); // 최대 스레드 수
    executor.setQueueCapacity(100); // 대기 큐
    executor.setThreadNamePrefix("NotificationExecutor-");
    executor.initialize();
    return executor;
  }

  @Bean(name = "externalApiExecutor")
  public Executor externalApiExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(15);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("ExternalApiExecutor-");
    executor.initialize();
    return executor;
  }
}
