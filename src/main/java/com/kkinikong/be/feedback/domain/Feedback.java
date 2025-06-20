package com.kkinikong.be.feedback.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.user.domain.User;

@Table(name = "feedbacks")
@Entity
@Getter
@NoArgsConstructor
public class Feedback extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "rating", nullable = false)
  private int rating;

  @Column(name = "content", length = 6000)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  public Feedback(int rating, String content, User user) {
    this.rating = rating;
    this.content = content;
    this.user = user;
  }
}
