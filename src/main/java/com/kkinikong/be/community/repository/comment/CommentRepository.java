package com.kkinikong.be.community.repository.comment;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

  @EntityGraph(attributePaths = {"commentLikeList", "commentLikeList.user"})
  List<Comment> findAllByCommunityPostId(Long postId);
}
