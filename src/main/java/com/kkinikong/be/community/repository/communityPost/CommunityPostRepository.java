package com.kkinikong.be.community.repository.communityPost;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.type.Category;

@Repository
public interface CommunityPostRepository
    extends JpaRepository<CommunityPost, Long>, CommunityPostCustomRepository {

  List<CommunityPost> findTop5ByOrderByLikeCountDescViewCountDesc();

  Page<CommunityPost> findAllByCategory(Category category, Pageable pageable);

  Page<CommunityPost> findAll(Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM CommunityPost p WHERE p.id = :postId")
  Optional<CommunityPost> findByIdForUpdate(@Param("postId") Long postId);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE CommunityPost p SET p.viewCount = p.viewCount + :count WHERE p.id = :postId")
  void incrementViews(@Param("postId") Long postId, @Param("count") Long count);

  @EntityGraph(attributePaths = {"communityPostImageList"})
  List<CommunityPost> findByIdIn(List<Long> ids);
}
