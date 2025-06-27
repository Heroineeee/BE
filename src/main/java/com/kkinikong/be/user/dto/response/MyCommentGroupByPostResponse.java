package com.kkinikong.be.user.dto.response;

import java.util.List;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.util.TimeUtil;

public record MyCommentGroupByPostResponse(
    Long postId,
    String titlePreview,
    String contentPreview,
    String createdAt,
    Long commentCount,
    Long likeCount,
    Long viewCount,
    List<MyCommentInPostResponse> myCommentList) {
  public static MyCommentGroupByPostResponse from(CommunityPost post, List<Comment> comments) {
    return new MyCommentGroupByPostResponse(
        post.getId(),
        shortContent(post.getTitle(), 25),
        shortContent(post.getContent(), 50),
        TimeUtil.relativeTimeFormatter(post.getCreatedDate()),
        post.getCommentCount(),
        post.getLikeCount(),
        post.getViewCount(),
        comments.stream().map(MyCommentInPostResponse::from).toList());
  }

  private static String shortContent(String content, int maxLength) {
    return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
  }
}
