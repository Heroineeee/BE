package com.kkinikong.be.review.repository.reviewtag;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.review.domain.ReviewTag;

public interface ReviewTagRepository
    extends JpaRepository<ReviewTag, Long>, ReviewTagRepositoryCustom {}
