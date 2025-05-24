package com.kkinikong.be.convenience.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;
import com.kkinikong.be.global.entity.BaseEntity;

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

  @Column(name = "decription", nullable = false)
  private String decription;

  @Column(name = "is_available", nullable = false)
  private Boolean isAvailable;

  @Column(name = "correct_count", nullable = false)
  private long correctCount = 0L;

  @Column(name = "incorrect_count", nullable = false)
  private long incorrectCount = 0L;

  @OneToMany(mappedBy = "conveniencePost", cascade = CascadeType.ALL)
  private List<ConvenienceHelpful> helpfulList = new ArrayList<>();
}
