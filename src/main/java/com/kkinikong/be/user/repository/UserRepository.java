package com.kkinikong.be.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.domain.type.LoginType;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findUserById(Long id);

  Optional<User> findByEmailAndLoginType(String email, LoginType loginType);

  boolean existsByEmail(String email);
}
