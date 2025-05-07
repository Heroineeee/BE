package com.kkinikong.be.store.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.review.domain.Review;
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

  @Column(name = "region", nullable = false)
  private String region;

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
  private double ratingAvg = 0.0;

  @Column(name = "scrap_count", nullable = false)
  private long scrapCount = 0L;

  @Column(name = "review_count", nullable = false)
  private long reviewCount = 0L;

  @Column(name = "view_count", nullable = false)
  private long viewCount = 0L;

  @Column(name = "updated_date", nullable = false)
  private LocalDate updatedDate;

  @Transient private Boolean isScrapped;

  public void setIsScrapped(Boolean isScrapped) {
    this.isScrapped = isScrapped;
  }

  public Boolean getIsScrapped() {
    return isScrapped;
  }

  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviewList = new ArrayList<>();

  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StoreScrap> storeScrapList = new ArrayList<>();

  // CSV 매핑 전용 빌더
  @Builder
  private Store(
      String name,
      String region,
      Category category,
      String address,
      double latitude,
      double longitude,
      LocalDate updatedDate) {
    this.name = name;
    this.region = region;
    this.category = category;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
    this.updatedDate = updatedDate;
    this.ratingAvg = 0.0;
    this.scrapCount = 0L;
    this.reviewCount = 0L;
  }
}
