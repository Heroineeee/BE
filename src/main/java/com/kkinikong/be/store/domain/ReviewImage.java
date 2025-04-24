package com.kkinikong.be.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;

@Table(name = "review_images")
@Entity
@Getter
@NoArgsConstructor
public class ReviewImage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Column(name = "image_order", nullable = false)
  private Integer imageOrder; // 이미지 순서

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;
}
