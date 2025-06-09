package com.kkinikong.be.commuity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.commuity.domain.CommunityPostLike;

@Repository
public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {}
