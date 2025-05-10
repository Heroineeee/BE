package com.kkinikong.be.report.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

  @Transactional
  public void reportStore(
      Long storeId, StoreReportReason storeReportReason, ReportRequest reportRequest, Long userId) {
    User user = findUserOrThrow(userId);

    if (reportRepository.existsReportByTargetIdAndUserId(storeId, user.getId())) {
      throw new ReportException(ReportErrorCode.REPORT_ALREADY_EXISTS);
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

    if (reportRepository.existsReportByTargetIdAndUserId(reviewId, user.getId())) {
      throw new ReportException(ReportErrorCode.REPORT_ALREADY_EXISTS);
    }

    if (review.getUser().getId().equals(user.getId())) {
      throw new ReportException(ReportErrorCode.REPORT_SELF);
    }

    Report report =
        Report.builder()
            .targetId(reviewId)
            .reportType(ReportType.REVIEW)
            .reason(CommonReportReason.toString())
            .description(
                CommonReportReason.equals(
                        com.kkinikong.be.report.domain.type.CommonReportReason.ETC)
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
}
