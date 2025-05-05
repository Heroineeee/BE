package com.kkinikong.be.review.domain.mapping;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.ReviewTag;

@Table(
    name = "review_tag_map",
    uniqueConstraints = @UniqueConstraint(columnNames = {"review_id", "tag_id"}))
@Entity
@Getter
@NoArgsConstructor
public class ReviewTagMap {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private ReviewTag reviewTag;
}
