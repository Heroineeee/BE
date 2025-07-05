package com.kkinikong.be.report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;
import com.kkinikong.be.community.repository.comment.CommentRepository;
import com.kkinikong.be.community.repository.communityPost.CommunityPostRepository;
import com.kkinikong.be.report.domain.Report;
import com.kkinikong.be.report.domain.type.CommonReportReason;
import com.kkinikong.be.report.domain.type.ReportType;
import com.kkinikong.be.report.domain.type.StoreReportReason;
import com.kkinikong.be.report.dto.request.ReportRequest;
import com.kkinikong.be.report.exception.ReportException;
import com.kkinikong.be.report.exception.errorcode.ReportErrorCode;
import com.kkinikong.be.report.respository.ReportRepository;
import com.kkinikong.be.review.domain.Review;
import com.kkinikong.be.review.exception.ReviewException;
import com.kkinikong.be.review.exception.errorcode.ReviewErrorCode;
import com.kkinikong.be.review.repository.ReviewRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportService {

  private final ReportRepository reportRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;
  private final CommunityPostRepository communityPostRepository;
  private final CommentRepository commentRepository;

  @Transactional
  public void reportStore(
      Long storeId, StoreReportReason storeReportReason, ReportRequest reportRequest, Long userId) {
    User user = findUserOrThrow(userId);

    if (reportRepository.existsReportByTargetIdAndUserIdAndCreatedDateAfter(
        storeId, user.getId(), LocalDateTime.now().minusDays(7))) {
      throw new ReportException(ReportErrorCode.STORE_REPORT_ALREADY_EXISTS);
    }

    Report report =
        Report.builder()
            .targetId(storeId)
            .reportType(ReportType.STORE)
            .reason(storeReportReason.toString())
            .description(
                storeReportReason.equals(StoreReportReason.ETC)
                    ? reportRequest.description()
                    : null)
            .user(user)
            .build();

    reportRepository.save(report);
  }

  @Transactional
  public void reportReview(
      Long reviewId,
      CommonReportReason CommonReportReason,
      ReportRequest reportRequest,
      Long userId) {
    User user = findUserOrThrow(userId);
    Review review = findReviewOrThrow(reviewId);

    checkSelfReport(review.getUser().getId(), userId);
    checkTargetExist(ReportType.REVIEW, reviewId, userId);

    saveReport(reviewId, ReportType.REVIEW, CommonReportReason, reportRequest, user);
  }

  @Transactional
  public void reportCommunityPost(
      Long postId,
      CommonReportReason CommonReportReason,
      ReportRequest reportRequest,
      Long userId) {
    User user = findUserOrThrow(userId);
    CommunityPost communityPost = findCommunityPostOrThrow(postId);

    checkSelfReport(communityPost.getUser().getId(), userId);
    checkTargetExist(ReportType.COMMUNITY_POST, communityPost.getId(), userId);

    communityPost.incrementReportCount();

    saveReport(postId, ReportType.COMMUNITY_POST, CommonReportReason, reportRequest, user);
  }

  @Transactional
  public void reportComment(
      Long commentId,
      CommonReportReason CommonReportReason,
      ReportRequest reportRequest,
      Long userId) {
    User user = findUserOrThrow(userId);

    Comment comment = findCommentOrThrow(commentId);

    checkSelfReport(comment.getUser().getId(), userId);
    checkTargetExist(ReportType.COMMUNITY_COMMENT, comment.getId(), userId);

    comment.incrementReportCount();

    saveReport(commentId, ReportType.COMMUNITY_COMMENT, CommonReportReason, reportRequest, user);
  }

  private void saveReport(
      Long targetId,
      ReportType reportType,
      CommonReportReason commonReportReason,
      ReportRequest reportRequest,
      User user) {
    Report report =
        Report.builder()
            .targetId(targetId)
            .reportType(reportType)
            .reason(commonReportReason.toString())
            .description(
                commonReportReason.equals(CommonReportReason.ETC)
                    ? reportRequest.description()
                    : null)
            .user(user)
            .build();

    reportRepository.save(report);
  }

  private User findUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }

  private Review findReviewOrThrow(Long reviewId) {
    return reviewRepository
        .findById(reviewId)
        .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));
  }

  private CommunityPost findCommunityPostOrThrow(Long postId) {
    return communityPostRepository
        .findById(postId)
        .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMUNITY_POST_NOT_FOUND));
  }

  private Comment findCommentOrThrow(Long commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));
  }

  // 이미 신고한 대상인지 확인
  private void checkTargetExist(ReportType reportType, Long targetId, Long userId) {
    if (reportRepository.existsReportByReportTypeAndTargetIdAndUserId(
        reportType, targetId, userId)) {
      throw new ReportException(ReportErrorCode.REPORT_ALREADY_EXISTS);
    }
  }

  // 자기 자신을 신고하는지 확인
  private void checkSelfReport(Long ownerId, Long userId) {
    if (ownerId.equals(userId)) {
      throw new ReportException(ReportErrorCode.SELF_REPORT);
    }
  }
}
