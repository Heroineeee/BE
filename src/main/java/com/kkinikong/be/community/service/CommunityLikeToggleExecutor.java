package com.kkinikong.be.community.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.CommunityPostLike;
import com.kkinikong.be.community.dto.response.LikeToggleResponse;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;
import com.kkinikong.be.community.repository.CommunityPostLikeRepository;
import com.kkinikong.be.community.repository.communityPost.CommunityPostRepository;
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
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public LikeToggleResponse togglePostLikeOnce(Long postId, Long userId) {

    CommunityPost post =
        communityPostRepository
            .findById(postId)
            .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

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
}
