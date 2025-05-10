package com.kkinikong.be.review.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.review.domain.type.Tag;

@Table(name = "review_tags")
@Entity
@Getter
@NoArgsConstructor
public class ReviewTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tag", nullable = false)
  @Enumerated(EnumType.STRING)
  private Tag tag;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;
}
