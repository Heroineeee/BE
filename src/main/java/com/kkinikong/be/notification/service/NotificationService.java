package com.kkinikong.be.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.notification.domain.Notification;
import com.kkinikong.be.notification.dto.response.NotificationResponse;
import com.kkinikong.be.notification.exception.NotificationException;
import com.kkinikong.be.notification.exception.errorcode.NotificationErrorCode;
import com.kkinikong.be.notification.repository.NotificationRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  public PageResponse<NotificationResponse> getNotificationList(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    User user = getUserOrThrow(userId);
    Page<Notification> notificationPage = notificationRepository.findAllByReceiver(user, pageable);
    return PageResponse.from(notificationPage, NotificationResponse::from);
  }

  @Transactional
  public void markAsRead(Long userId, Long notificationId) {
    User user = getUserOrThrow(userId);
    Notification notification = getNotificationOrThrow(notificationId);
    validationNotificationOwner(user, notification);
    notification.markAsRead();
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }

  private Notification getNotificationOrThrow(Long notificationId) {
    return notificationRepository
        .findById(notificationId)
        .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
  }

  private void validationNotificationOwner(User user, Notification notification) {
    if (!notification.getReceiver().getId().equals(user.getId())) {
      throw new NotificationException(NotificationErrorCode.FORBIDDEN_ACCESS);
    }
  }
}
