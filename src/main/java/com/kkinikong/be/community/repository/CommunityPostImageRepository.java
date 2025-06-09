package com.kkinikong.be.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.CommunityPostImage;

@Repository
public interface CommunityPostImageRepository extends JpaRepository<CommunityPostImage, Long> {}
