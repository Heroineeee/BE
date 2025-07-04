package com.kkinikong.be.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.notification.domain.Notification;
import com.kkinikong.be.notification.dto.response.NotificationResponse;
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

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
