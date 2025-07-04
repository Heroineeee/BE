package com.kkinikong.be.notification.dto.response;

import com.kkinikong.be.notification.domain.type.NotificationType;

public record NotificationMessage(
    Long receiverId, NotificationType type, String content, String redirectUrl, Long targetId) {}
