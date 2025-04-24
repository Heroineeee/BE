package com.kkinikong.be.store.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
  private double ratingAvg;

  @Column(name = "scrap_count", nullable = false)
  private long scarpCount = 0L;

  @Column(name = "review_count", nullable = false)
  private long reviewCount = 0L;

  @Column(name = "updated_date", nullable = false)
  private LocalDate updatedDate;

  @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviewList = new ArrayList<>();
}
