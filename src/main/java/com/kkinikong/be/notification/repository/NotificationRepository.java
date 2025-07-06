package com.kkinikong.be.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  Page<Notification> findAllByReceiverIdOrderByCreatedDateDesc(Long receiverId, Pageable pageable);
}
