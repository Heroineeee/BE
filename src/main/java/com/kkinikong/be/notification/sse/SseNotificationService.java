package com.kkinikong.be.notification.sse;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.notification.domain.Notification;
import com.kkinikong.be.notification.domain.type.NotificationType;
import com.kkinikong.be.notification.dto.response.NotificationResponse;
import com.kkinikong.be.notification.repository.NotificationRepository;
import com.kkinikong.be.user.domain.User;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SseNotificationService {

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

  private final EmitterRepository emitterRepository;
  private final NotificationRepository notificationRepository;

  public SseEmitter subscribe(Long userId, String lastEventId) {
    String emitterId = makeEmitterId(userId);
    SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
    log.info("✅ SSE 연결 생성: emitterId = {}", emitterId);

    // 상황 별 emitter 삭제 처리
    emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
    emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
    emitter.onError((e) -> emitterRepository.deleteById(emitterId));

    // 더미 이벤트 전송
    String eventId = makeEventId(userId);
    sendToClient(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

    // 미수신 이벤트 전송
    if (hasLostData(lastEventId)) {
      sendLostData(lastEventId, userId, emitterId, emitter);
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

    NotificationResponse response = NotificationResponse.from(notification);
    String userId = String.valueOf(receiver.getId());
    String eventId = makeEventId(receiver.getId());

    Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(userId);
    emitters.forEach(
        (key, emitter) -> {
          emitterRepository.saveEventCache(key, response);
          sendToClient(emitter, eventId, key, response);
        });
  }

  private void sendToClient(SseEmitter emitter, String eventId, String emitterId, Object data) {
    try {
      emitter.send(SseEmitter.event().id(eventId).name("notification").data(data));
    } catch (IOException e) {
      emitterRepository.deleteById(emitterId);
    }
  }

  private boolean hasLostData(String lastEventId) {
    return lastEventId != null && !lastEventId.isEmpty();
  }

  private void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
    Map<String, Object> eventCaches =
        emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
    eventCaches.entrySet().stream()
        .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
        .forEach(entry -> sendToClient(emitter, entry.getKey(), emitterId, entry.getValue()));
  }

  private String makeEmitterId(Long userId) {
    return userId + "_" + System.currentTimeMillis();
  }

  private String makeEventId(Long userId) {
    return userId + "_" + System.currentTimeMillis();
  }
}
