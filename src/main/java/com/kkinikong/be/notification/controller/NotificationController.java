package com.kkinikong.be.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.notification.sse.SseNotificationService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
@RequestMapping("/api/v1/notification")
public class NotificationController {

  private final SseNotificationService notificationService;

  @Operation(summary = "SSE 연결")
  @GetMapping("/subscribe")
  public ResponseEntity<SseEmitter> subscribe(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "")
          String lastEventId) {
    SseEmitter emitter = notificationService.subscribe(userDetails.getId(), lastEventId);
    return ResponseEntity.ok(emitter);
  }
}
