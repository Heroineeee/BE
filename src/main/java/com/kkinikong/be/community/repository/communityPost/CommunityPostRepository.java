package com.kkinikong.be.community.repository.communityPost;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.type.Category;

@Repository
public interface CommunityPostRepository
    extends JpaRepository<CommunityPost, Long>, CommunityPostCustomRepository {

  @Query(
      """
      SELECT cp FROM CommunityPost cp
      LEFT JOIN CommunityPostLike cpl ON cp.id = cpl.communityPost.id
      WHERE cpl.createdDate >= :since
      GROUP BY cp.id
      ORDER BY COUNT(cpl.id) DESC, cp.viewCount DESC
      LIMIT 3
      """)
  List<CommunityPost> findTop3ByLikesSince72Hours(@Param("since") LocalDateTime since);

  @EntityGraph(attributePaths = {"communityPostImageList"})
  Page<CommunityPost> findAllByCategory(Category category, Pageable pageable);

  @EntityGraph(attributePaths = {"communityPostImageList"})
  Page<CommunityPost> findAll(Pageable pageable);

  Page<CommunityPost> findAllByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE CommunityPost p SET p.viewCount = p.viewCount + :count WHERE p.id = :postId")
  void incrementViews(@Param("postId") Long postId, @Param("count") Long count);

  @EntityGraph(attributePaths = {"communityPostImageList"})
  List<CommunityPost> findByIdIn(List<Long> ids);
}
