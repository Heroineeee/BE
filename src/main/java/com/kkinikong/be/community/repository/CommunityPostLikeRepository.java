package com.kkinikong.be.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.CommunityPostLike;

@Repository
public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {}
