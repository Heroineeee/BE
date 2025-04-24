package com.kkinikong.be.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.store.domain.type.Category;

@Table(name = "stores")
@Entity
@Getter
@NoArgsConstructor
public class Store extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "category", nullable = false)
  @Enumerated(EnumType.STRING)
  private Category category;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "latitude", nullable = false)
  private double latitude;

  @Column(name = "longitude", nullable = false)
  private double longitude;

  @Column(name = "rating_avg", nullable = false)
  private float ratingAvg;

  @Column(name = "like_count", nullable = false)
  private Long likeCount;

  @Column(name = "review_count", nullable = false)
  private Long reviewCount;
}
