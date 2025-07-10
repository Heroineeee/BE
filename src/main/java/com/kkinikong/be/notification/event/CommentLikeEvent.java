package com.kkinikong.be.notification.event;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.user.domain.User;

public record CommentLikeEvent(User receiver, User sender, Comment comment) {}
