package com.kkinikong.be.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.user.domain.User;

@Table(
    name = "scraps",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "store_id"}))
@Entity
@Getter
@NoArgsConstructor
public class StoreScrap extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;
}
