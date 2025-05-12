package com.kkinikong.be.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.review.domain.ReviewTag;

@Repository
public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {

  List<ReviewTag> findAllByReviewId(Long reviewId);

  void deleteByReviewId(Long reviewId);
}
