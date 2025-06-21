package com.kkinikong.be.community.dto.response;

import java.util.List;

public record CommunityPostPopularWrappingResponse(
    List<CommunityPostPopularResponse> communityPostPopularResponses) {}
