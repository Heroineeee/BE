package com.kkinikong.be.review.repository.reviewtag;

import java.util.List;
import java.util.Map;

import com.kkinikong.be.review.domain.type.Tag;

public interface ReviewTagCustomRepository {

  Map<Long, List<Tag>> getReviewTagsByReviewIds(List<Long> reviewIds);
}
