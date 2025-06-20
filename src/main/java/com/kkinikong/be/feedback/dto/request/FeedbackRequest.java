package com.kkinikong.be.feedback.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(@Min(1) @Max(5) int rating, @Size(max = 6000) String content) {}
