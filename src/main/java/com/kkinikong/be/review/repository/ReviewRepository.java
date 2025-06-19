package com.kkinikong.be.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.review.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  boolean existsReviewByUserIdAndStoreId(Long userId, Long storeId);

  Page<Review> findAllByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

  Page<Review> findReviewsByStoreId(Long storeId, Pageable pageable);
}
