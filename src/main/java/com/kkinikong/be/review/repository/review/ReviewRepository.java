package com.kkinikong.be.review.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.review.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  boolean existsReviewByUserIdAndStoreId(Long userId, Long storeId);
}
