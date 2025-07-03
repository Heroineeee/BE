package com.kkinikong.be.notification.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.notification.domain.type.NotificationType;
import com.kkinikong.be.user.domain.User;

@Table(name = "notification")
@Entity
@Getter
@NoArgsConstructor
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User receiver;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private NotificationType notificationType;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "target_id")
  private Long targetId;

  @Column(name = "redirect_url")
  private String redirectUrl;

  @Column(name = "is_read", nullable = false)
  private boolean isRead = false;

  public void markAsRead() {
    this.isRead = true;
  }

  @Builder
  public Notification(
      User receiver, NotificationType type, String content, Long targetId, String redirectUrl) {
    this.receiver = receiver;
    this.notificationType = type;
    this.content = content;
    this.targetId = targetId;
    this.redirectUrl = redirectUrl;
    this.isRead = false;
  }
}
