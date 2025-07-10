package com.kkinikong.be.notification.event;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.user.domain.User;

public record CommunityLikeEvent(User receiver, User sender, CommunityPost post) {}
