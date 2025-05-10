package com.kkinikong.be.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.review.domain.type.Tag;

@Table(
    name = "store_tag_counts",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "idx_store_tag",
          columnNames = {"store_id", "tag"})
    })
@Entity
@Getter
@NoArgsConstructor
public class StoreTagCount extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @Enumerated(EnumType.STRING)
  @Column(name = "tag", nullable = false)
  private Tag tag;

  private Long count = 1L;

  public void incrementCount() {
    this.count++;
  }

  @Builder
  public StoreTagCount(Store store, Tag tag) {
    this.store = store;
    this.tag = tag;
  }
}
