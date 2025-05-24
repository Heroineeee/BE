package com.kkinikong.be.convenience.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.user.domain.User;

@Entity
@Table(name = "convenience_helpful")
@Getter
@NoArgsConstructor
public class ConvenienceHelpful extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "is_correct", nullable = false)
  private Boolean isCorrect;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "convenience_post_id", nullable = false)
  private ConveniencePost conveniencePost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Builder
  public ConvenienceHelpful(User user, ConveniencePost conveniencePost, Boolean isCorrect) {
    this.user = user;
    this.conveniencePost = conveniencePost;
    this.isCorrect = isCorrect;
  }

  public void updateIsCorrect(Boolean isCorrect) {
    this.isCorrect = isCorrect;
  }
}
