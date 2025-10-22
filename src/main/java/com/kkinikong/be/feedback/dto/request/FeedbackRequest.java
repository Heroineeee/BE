package com.kkinikong.be.feedback.dto.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import com.kkinikong.be.feedback.domain.type.FeedbackType;

public record FeedbackRequest(
    @Min(1) @Max(5) int rating, @Size(max = 6000) String content, List<FeedbackType> type) {}
