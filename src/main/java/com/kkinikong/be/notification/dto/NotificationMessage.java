package com.kkinikong.be.notification.dto;

import com.kkinikong.be.notification.domain.type.NotificationType;

public record NotificationMessage(
    Long receiverId, NotificationType type, String content, String redirectUrl, Long targetId) {}
