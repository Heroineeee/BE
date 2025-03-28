package com.utopia.utopia_be.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.utopia.utopia_be.global.entity.BaseEntity;
import com.utopia.utopia_be.user.domain.type.LoginType;
import com.utopia.utopia_be.user.domain.type.Role;

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

  @Column(name = "login_type", nullable = false)
  private LoginType loginType;

  @Column(name = "sincerity_score", nullable = false)
  private Long sincerityScore = 50L;

  @Column(name = "role", nullable = false)
  private Role role = Role.ROLE_USER;

  @Builder
  public User(String email, LoginType loginType) {
    this.email = email;
    this.loginType = loginType;
    if (loginType == LoginType.BASIC) {
      this.role = Role.ROLE_ADMIN;
    }
  }
}
