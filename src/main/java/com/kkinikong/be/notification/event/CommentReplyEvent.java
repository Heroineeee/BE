package com.kkinikong.be.notification.event;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.user.domain.User;

public record CommentReplyEvent(User receiver, User sender, CommunityPost post, Comment reply) {}
