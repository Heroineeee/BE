package com.kkinikong.be.community.repository.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;

import com.kkinikong.be.community.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

  @EntityGraph(attributePaths = {"commentLikeList", "commentLikeList.user"})
  List<Comment> findAllByCommunityPostId(Long postId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM Comment c WHERE c.id = :commentId")
  Optional<Comment> findByIdForUpdate(@Param("commentId") Long commentId);
}
