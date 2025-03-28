package com.utopia.utopia_be.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utopia.utopia_be.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}
