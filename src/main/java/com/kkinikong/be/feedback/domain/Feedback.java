package com.kkinikong.be.feedback.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.feedback.domain.type.FeedbackType;
import com.kkinikong.be.global.entity.BaseEntity;

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

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private FeedbackType type;

  @Builder
  public Feedback(int rating, String content, FeedbackType type) {
    this.rating = rating;
    this.content = content;
    this.type = type;
  }
}
