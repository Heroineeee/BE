package com.kkinikong.be.global.logging;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

  @Around("execution(* com.kkinikong.be..*Service.*(..))")
  public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    String methodName = joinPoint.getSignature().toShortString();
    try {
      Object result = joinPoint.proceed();
      long duration = System.currentTimeMillis() - start;
      log.info("⏱️ 비즈니스 로직: {} 실행 시간 {}ms", methodName, duration);
      return result;
    } catch (Throwable ex) {
      long duration = System.currentTimeMillis() - start;
      log.warn("⏱️ 비즈니스 로직: {} 실행 시간 {}ms (예외 발생: {})", methodName, duration, ex.getClass().getSimpleName());
      throw ex;
    }
  }

  @AfterReturning(
      pointcut = "execution(* com.kkinikong.be..*Repository.*(..))",
      returning = "result")
  public void logRepositoryExecution(JoinPoint joinPoint, Object result) {
    String methodName = joinPoint.getSignature().toShortString();
    String resultType = (result != null) ? result.getClass().getSimpleName() : "void";
    log.info("💾 데이터 접근: {} 성공 | 반환 값 타입: {}", methodName, resultType);
  }
}
