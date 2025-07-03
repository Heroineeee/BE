package com.kkinikong.be.notification.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.notification.domain.Notification;
import com.kkinikong.be.notification.domain.type.NotificationType;
import com.kkinikong.be.notification.repository.NotificationRepository;
import com.kkinikong.be.notification.sse.EmitterRepository;
import com.kkinikong.be.user.domain.User;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

  private final EmitterRepository emitterRepository;
  private final NotificationRepository notificationRepository;

  public SseEmitter subscribe(Long userId, String lastEventId) {
    String emitterId = userId + "_" + System.currentTimeMillis();
    SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
    log.info("✅ SSE 연결 생성: emitterId = {}", emitterId);

    // 상황 별 emitter 삭제 처리
    emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
    emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
    emitter.onError((e) -> emitterRepository.deleteById(emitterId));

    // 더미 이벤트 전송
    sendToClient(emitter, emitterId, "EventStream Created. [userId=" + userId + "]");

    // 미전송된 이벤트가 있다면 전송
    if (!lastEventId.isEmpty()) {
      Map<String, Object> eventCache =
          emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
      eventCache.entrySet().stream()
          .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
          .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
    }

    return emitter;
  }

  public void send(
      User receiver, NotificationType type, String content, Long targetId, String redirectUrl) {
    Notification notification =
        notificationRepository.save(
            Notification.builder()
                .receiver(receiver)
                .type(type)
                .content(content)
                .targetId(targetId)
                .redirectUrl(redirectUrl)
                .build());

    String userId = String.valueOf(receiver.getId());
    Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(userId);

    emitters.forEach(
        (key, emitter) -> {
          emitterRepository.saveEventCache(key, notification);
          sendToClient(emitter, key, notification);
        });
  }

  private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
    try {
      emitter.send(SseEmitter.event().id(emitterId).name("notification").data(data));
    } catch (IOException e) {
      emitterRepository.deleteById(emitterId);
    }
  }
}
