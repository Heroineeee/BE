package com.kkinikong.be.review.repository.reviewtag;

import java.util.List;
import java.util.Map;

public interface ReviewTagRepositoryCustom {
  Map<Long, String> findRepresentativeTagByStoreId(List<Long> storeIds);
}
