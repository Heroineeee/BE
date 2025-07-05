package com.kkinikong.be.notification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.notification.infrastructure.sse.SseNotificationService;
import com.kkinikong.be.notification.service.NotificationService;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
@RequestMapping("/api/v1/notification")
public class NotificationController {

  private final SseNotificationService sseNotificationService;
  private final NotificationService notificationService;

  @Operation(
      summary = "SSE 연결",
      description =
          """
  - 클라이언트에서 서버와의 SSE 연결을 맺어 실시간으로 알림을 수신합니다.
  - 연결이 끊겼을 경우 `Last-Event-ID`를 이용해 수신하지 못한 알림을 이어받을 수 있습니다.""")
  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<SseEmitter> subscribe(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "")
          String lastEventId) {
    SseEmitter emitter = sseNotificationService.subscribe(userDetails.getId(), lastEventId);
    return ResponseEntity.ok(emitter);
  }

  @Operation(
      summary = "알림 조회",
      description =
          """
  - 로그인한 사용자의 알림 목록을 조회하는 API입니다.
  - 기본적으로 최신순으로 정렬됩니다.
  - 알림의 읽음 여부는 true(읽음), false(읽지 않음)으로 나타납니다.""")
  @GetMapping("")
  public ResponseEntity<ApiResponse<Object>> getNotification(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponse.from(
                notificationService.getNotificationList(userDetails.getId(), page, size)));
  }

  @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리하는 API입니다.")
  @PatchMapping("/{notificationId}/read")
  public ResponseEntity<ApiResponse<Object>> readNotification(
      @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long notificationId) {
    notificationService.markAsRead(userDetails.getId(), notificationId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
