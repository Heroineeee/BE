package com.kkinikong.be.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.convenience.domain.ConvenienceHelpful;
import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.report.domain.Report;
import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.store.domain.StoreScrap;
import com.kkinikong.be.user.domain.type.LoginType;
import com.kkinikong.be.user.domain.type.Role;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "nickname", unique = true)
  private String nickname;

  @Column(name = "password")
  private String password;

  @Column(name = "login_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private LoginType loginType;

  @Column(name = "role", nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "place_latitude")
  private Double placeLatitude;

  @Column(name = "place_longitude")
  private Double placeLongitude;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Builder(builderMethodName = "socialLoginBuilder", buildMethodName = "buildSocialLogin")
  public User(String email, LoginType loginType) {
    this.email = email;
    this.loginType = loginType;
    this.role = Role.ROLE_USER;
  }

  @Builder(builderMethodName = "basicLoginBuilder", buildMethodName = "buildBasicLogin")
  public User(String email, String password) {
    this.email = email;
    this.password = password;
    this.loginType = LoginType.BASIC;
    this.role = Role.ROLE_ADMIN;
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }

  public void updatePlace(Double latitude, Double longitude) {
    this.placeLatitude = latitude;
    this.placeLongitude = longitude;
  }

  public void withdraw() {
    this.email = "deleted_" + UUID.randomUUID() + "@deleted.com";
    this.nickname = "DeletedUser_" + UUID.randomUUID().toString().substring(0, 8);
    this.isDeleted = true;
    this.deletedAt = LocalDateTime.now();
    this.placeLatitude = null;
    this.placeLongitude = null;
  }

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviewList = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StoreScrap> storeScrapList = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Report> reportList = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ConveniencePost> conveniencePostList = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ConvenienceHelpful> helpfulList = new ArrayList<>();
}
