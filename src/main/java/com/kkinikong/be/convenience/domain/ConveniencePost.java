package com.kkinikong.be.convenience.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;
import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.user.domain.User;

@Table(name = "convenience_post")
@Entity
@Getter
@NoArgsConstructor
public class ConveniencePost extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "brand", nullable = false)
  private Brand brand;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private Category category;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "is_available", nullable = false)
  private Boolean isAvailable;

  @Column(name = "correct_count", nullable = false)
  private long correctCount = 0L;

  @Column(name = "incorrect_count", nullable = false)
  private long incorrectCount = 0L;

  @OneToMany(mappedBy = "conveniencePost", cascade = CascadeType.ALL)
  private List<ConvenienceHelpful> helpfulList = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Builder
  public ConveniencePost(
      String name,
      Brand brand,
      Category category,
      String description,
      Boolean isAvailable,
      User user) {
    this.name = name;
    this.brand = brand;
    this.category = category;
    this.description = description;
    this.isAvailable = isAvailable;
    this.user = user;
  }

  public void increaseCount(boolean isCorrect) {
    if (isCorrect) this.correctCount++;
    else this.incorrectCount++;
  }

  public void decreaseCount(boolean isCorrect) {
    if (isCorrect && correctCount > 0) this.correctCount--;
    else if (!isCorrect && incorrectCount > 0) this.incorrectCount--;
  }
}
