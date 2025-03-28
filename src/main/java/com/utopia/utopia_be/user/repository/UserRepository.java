package com.utopia.utopia_be.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utopia.utopia_be.user.domain.User;
import com.utopia.utopia_be.user.domain.type.LoginType;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findUserById(Long id);

  Optional<User> findByEmailAndLoginType(String email, LoginType loginType);

  Optional<Boolean> existsByEmail(String email);
}
