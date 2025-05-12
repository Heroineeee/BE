package com.kkinikong.be.review.repository.reviewimage;

import java.util.List;
import java.util.Map;

public interface ReviewImageCustomRepository {
  Map<Long, String> getReviewImageByReviewIds(List<Long> reviewIds);
}
