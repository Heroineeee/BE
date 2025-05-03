package com.kkinikong.be.store.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.store.domain.mapping.ReviewTagMap;
import com.kkinikong.be.user.domain.User;

@Table(name = "reviews")
@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "rating", nullable = false)
  private int rating;

  @Column(name = "content", nullable = false, length = 500)
  private String content;

  @Column(name = "like_count", nullable = false)
  private long likeCount = 0L;

  @Column(name = "is_certified", nullable = false)
  private boolean isCertified; // 아동급식카드 실 사용 인증 여부

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReviewTagMap> reviewTagMapList = new ArrayList<>();

  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReviewImage> reviewImageList = new ArrayList<>();
}
