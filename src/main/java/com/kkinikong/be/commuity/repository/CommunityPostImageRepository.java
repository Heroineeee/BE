package com.kkinikong.be.commuity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.commuity.domain.CommunityPostImage;

@Repository
public interface CommunityPostImageRepository extends JpaRepository<CommunityPostImage, Long> {}
