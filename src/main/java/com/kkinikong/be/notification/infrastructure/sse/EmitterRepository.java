package com.kkinikong.be.notification.infrastructure.sse;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
  SseEmitter save(String emitterId, SseEmitter sseEmitter);

  void saveEventCache(String emitterId, Object event);

  Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId);

  Map<String, Object> findAllEventCacheStartWithByUserId(String userId);

  void deleteById(String emitterId);

  void deleteAllEmitterStartWithByUserId(String userId);

  void deleteAllEventCacheStartWithByUserId(String userId);
}
