package com.kkinikong.be.user.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.store.domain.Review;
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

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviewList = new ArrayList<>();
}
