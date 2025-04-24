package com.kkinikong.be.store.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.store.domain.mapping.ReviewTagMap;
import com.kkinikong.be.store.domain.type.TagCategory;

@Table(name = "review_tags")
@Entity
@Getter
@NoArgsConstructor
public class ReviewTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "category", nullable = false)
  @Enumerated(EnumType.STRING)
  private TagCategory category;

  @OneToMany(mappedBy = "reviewTag")
  private List<ReviewTagMap> reviewTagMapList = new ArrayList<>();
}
