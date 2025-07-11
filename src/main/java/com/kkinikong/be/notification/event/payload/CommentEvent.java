package com.kkinikong.be.notification.event.payload;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.user.domain.User;

public record CommentEvent(User receiver, User sender, CommunityPost post, Comment comment) {}
