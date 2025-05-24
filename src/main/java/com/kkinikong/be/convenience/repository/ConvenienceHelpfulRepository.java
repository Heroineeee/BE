package com.kkinikong.be.convenience.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.convenience.domain.ConvenienceHelpful;
import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.user.domain.User;

@Repository
public interface ConvenienceHelpfulRepository extends JpaRepository<ConvenienceHelpful, Long> {
  Optional<ConvenienceHelpful> findByConveniencePostAndUser(
      ConveniencePost conveniencePost, User user);
}
