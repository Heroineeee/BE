package com.kkinikong.be.feedback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.feedback.dto.request.FeedbackRequest;
import com.kkinikong.be.feedback.service.FeedbackService;
import com.kkinikong.be.global.response.ApiResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
@RestController
@Tag(name = "Feedback", description = "피드백 관련 API")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @Operation(
      summary = "의견 남기기",
      description =
          "type에 SEARCH_FOOD(\"음식결과없음\")"
              + "  SEARCH_RESTAURANT(\"식당결과없음\"),"
              + "  SEARCH_COMMUNITY(\"커뮤니티결과없음\") 중 하나로 의견을 남깁니다.")
  @PostMapping("")
  public ResponseEntity<ApiResponse<Object>> addFeedback(@RequestBody FeedbackRequest request) {
    feedbackService.addFeedback(request);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
