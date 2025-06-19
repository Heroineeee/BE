package com.kkinikong.be.user.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.dto.response.ConveniencePostListResponse;
import com.kkinikong.be.convenience.repository.ConvenienceRepository;
import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.domain.type.Tag;
import com.kkinikong.be.review.repository.ReviewRepository;
import com.kkinikong.be.review.repository.reviewimage.ReviewImageRepository;
import com.kkinikong.be.review.repository.reviewtag.ReviewTagRepository;
import com.kkinikong.be.store.domain.StoreScrap;
import com.kkinikong.be.store.repository.storescrap.StoreScrapRepository;
import com.kkinikong.be.user.dto.response.MypageReviewResponse;
import com.kkinikong.be.user.dto.response.MypageStoreResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {
  private final StoreScrapRepository storeScrapRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewImageRepository reviewImageRepository;
  private final ReviewTagRepository reviewTagRepository;
  private final ConvenienceRepository convenienceRepository;

  public PageResponse<MypageStoreResponse> getScrapStore(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<StoreScrap> storeScrapPage =
        storeScrapRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);
    return PageResponse.from(storeScrapPage, scrap -> MypageStoreResponse.from(scrap.getStore()));
  }

  public PageResponse<MypageReviewResponse> getReview(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Review> reviewPage =
        reviewRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);

    Map<Long, String> reviewImageMap =
        reviewImageRepository.getReviewImageByReviewIds(reviewPage.map(Review::getId).getContent());
    Map<Long, List<Tag>> reviewTagMap =
        reviewTagRepository.getReviewTagsByReviewIds(reviewPage.map(Review::getId).getContent());

    return PageResponse.from(
        reviewPage,
        review ->
            MypageReviewResponse.from(
                review, reviewTagMap.get(review.getId()), reviewImageMap.get(review.getId())));
  }

  public PageResponse<ConveniencePostListResponse> getConveniencePost(
      Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ConveniencePost> conveniencePostPage =
        convenienceRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);
    return PageResponse.from(conveniencePostPage, ConveniencePostListResponse::from);
  }
}
