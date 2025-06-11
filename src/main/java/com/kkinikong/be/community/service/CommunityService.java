package com.kkinikong.be.community.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.CommunityPostImage;
import com.kkinikong.be.community.dto.request.CommunityPostRequest;
import com.kkinikong.be.community.dto.response.CommunityPostResponse;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;
import com.kkinikong.be.community.repository.CommunityPostImageRepository;
import com.kkinikong.be.community.repository.CommunityRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;
import com.kkinikong.be.util.s3.service.ImageService;
import com.kkinikong.be.util.s3.type.S3Bucket;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommunityService {

  private final CommunityRepository communityRepository;
  private final UserRepository userRepository;
  private final CommunityPostImageRepository communityPostImageRepository;

  private final ImageService imageService;

  @Transactional
  public CommunityPostResponse postCommunityPost(CommunityPostRequest request, Long userId) {
    CommunityPost communityPost =
        communityRepository.save(
            CommunityPost.builder()
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .user(getUserOrThrow(userId))
                .build());

    return new CommunityPostResponse(communityPost.getId());
  }

  @Transactional
  public void postCommunityPostImage(Long postId, List<MultipartFile> files, Long userId) {
    if (files == null || files.isEmpty()) return;
    if (files.size() > 3) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_IMAGE_SIZE_LIMIT);
    }
    if (communityPostImageRepository.existsByCommunityPostId(postId)) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_POST_IMAGE_ALREADY_EXISTS);
    }

    CommunityPost communityPost = getCommunityPostOrThrow(postId);
    validatePostOwner(communityPost, userId);

    List<String> imageUrl = imageService.uploadFileList(files, S3Bucket.COMMUNITY_POST_IMAGE);

    for (String url : imageUrl) {
      communityPostImageRepository.save(
          CommunityPostImage.builder().communityPost(communityPost).imageUrl(url).build());
    }
  }

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }

  private CommunityPost getCommunityPostOrThrow(Long reviewId) {
    return communityRepository
        .findById(reviewId)
        .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));
  }

  private void validatePostOwner(CommunityPost communityPost, Long userId) {
    if (!communityPost.getUser().getId().equals(userId)) {
      throw new CommunityException(CommunityErrorCode.COMMUNITY_NOT_OWNER);
    }
  }
}
