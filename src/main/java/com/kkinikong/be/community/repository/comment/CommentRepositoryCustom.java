package com.kkinikong.be.community.repository.comment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;

public interface CommentRepositoryCustom {
  List<Comment> findMyCommentsInPosts(Long userId, List<Long> postIds);

  Page<CommunityPost> findAllPostsWithMyComments(Long userId, Pageable pageable);

  void deleteAllCommentsAndLikesByPostId(Long postId);
}
