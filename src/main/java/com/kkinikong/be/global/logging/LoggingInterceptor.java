package com.kkinikong.be.global.logging;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

  private static final String START_TIME = "start_time";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    long startTime = System.currentTimeMillis();
    request.setAttribute(START_TIME, startTime);
    log.info("➡️ [{}] {} 요청 시작", request.getMethod(), request.getRequestURI());
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    Long start = (Long) request.getAttribute(START_TIME);
    long duration = start != null ? System.currentTimeMillis() - start : 0L;
    int status = response.getStatus();
    String method = request.getMethod();
    String uri = request.getRequestURI();

    if (status >= 500) {
      log.error("❌ [{}] {} 서버 오류 - {}ms | status={}", method, uri, duration, status);
    } else if (status >= 400) {
      log.warn("⚠️ [{}] {} 클라이언트 오류 - {}ms | status={}", method, uri, duration, status);
    } else {
      log.info("✅ [{}] {} 요청 완료 - {}ms | status={}", method, uri, duration, status);
    }
  }
}
