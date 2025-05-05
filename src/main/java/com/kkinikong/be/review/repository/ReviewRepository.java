package com.kkinikong.be.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {}
