package com.kkinikong.be.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.CommunityPostImage;

@Repository
public interface CommunityPostImageRepository extends JpaRepository<CommunityPostImage, Long> {
  boolean existsByCommunityPostId(Long postId);

  List<CommunityPostImage> findAllByCommunityPostId(Long postId);

  void deleteAllByCommunityPostId(Long postId);
}
