package com.kkinikong.be.community.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommentLike;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.CommunityPostLike;
import com.kkinikong.be.community.dto.response.LikeToggleResponse;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;
import com.kkinikong.be.community.repository.CommentLikeRepository;
import com.kkinikong.be.community.repository.CommunityPostLikeRepository;
import com.kkinikong.be.community.repository.comment.CommentRepository;
import com.kkinikong.be.community.repository.communityPost.CommunityPostRepository;
import com.kkinikong.be.notification.event.payload.CommentLikeEvent;
import com.kkinikong.be.notification.event.payload.CommunityLikeEvent;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CommunityLikeToggleExecutor {
  private final CommunityPostRepository communityPostRepository;
  private final CommunityPostLikeRepository communityPostLikeRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public LikeToggleResponse togglePostLikeOnce(Long postId, Long userId) {

    CommunityPost post =
        communityPostRepository
            .findById(postId)
            .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));
    User user = getUserOrThrow(userId);

    Optional<CommunityPostLike> existing =
        communityPostLikeRepository.findByCommunityPostIdAndUserId(postId, userId);

    boolean isLiked;
    if (existing.isPresent()) {
      communityPostLikeRepository.delete(existing.get());
      post.decrementLikeCount();
      isLiked = false;
    } else {
      communityPostLikeRepository.save(
          CommunityPostLike.builder().communityPost(post).user(user).build());
      post.incrementLikeCount();
      isLiked = true;
      if (!post.getUser().getId().equals(userId)) {
        eventPublisher.publishEvent(new CommunityLikeEvent(post.getUser(), user, post));
      }
    }
    communityPostRepository.saveAndFlush(post); // 버전 충돌 감지
    return LikeToggleResponse.from(isLiked, post.getLikeCount());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public LikeToggleResponse toggleCommentLikeOnce(Long commentId, Long userId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));

    User user = getUserOrThrow(userId);

    Optional<CommentLike> commentLike =
        commentLikeRepository.findByCommentIdAndUserId(commentId, userId);

    boolean isLiked;
    if (commentLike.isPresent()) {
      commentLikeRepository.delete(commentLike.get());
      comment.decrementLikeCount();
      isLiked = false;
    } else {
      commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build());
      comment.incrementLikeCount();
      isLiked = true;
      if (!comment.getUser().getId().equals(userId)) {
        eventPublisher.publishEvent(new CommentLikeEvent(comment.getUser(), user, comment));
      }
    }
    commentRepository.saveAndFlush(comment);
    return LikeToggleResponse.from(isLiked, comment.getLikeCount());
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
