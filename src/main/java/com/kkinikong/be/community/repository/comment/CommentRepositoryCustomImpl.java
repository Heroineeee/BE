package com.kkinikong.be.community.repository.comment;

import static com.kkinikong.be.community.domain.QComment.comment;
import static com.kkinikong.be.community.domain.QCommunityPost.communityPost;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.Comment;
import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.QComment;
import com.kkinikong.be.community.domain.QCommentLike;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QComment qComment = comment;
  private final QCommentLike commentLike = QCommentLike.commentLike;

  @Override
  public Page<CommunityPost> findAllPostsWithMyComments(Long userId, Pageable pageable) {
    List<CommunityPost> content =
        queryFactory
            .selectDistinct(communityPost)
            .from(comment)
            .join(qComment.communityPost, communityPost)
            .where(comment.user.id.eq(userId), comment.isDeleted.eq(false))
            .orderBy(communityPost.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Long total =
        queryFactory
            .select(communityPost.countDistinct())
            .from(comment)
            .join(comment.communityPost, communityPost)
            .where(comment.user.id.eq(userId), comment.isDeleted.eq(false))
            .fetchOne();

    return new PageImpl<>(content, pageable, total != null ? total : 0);
  }

  @Override
  public List<Comment> findMyCommentsInPosts(Long userId, List<Long> postIds) {
    return queryFactory
        .selectFrom(comment)
        .join(qComment.communityPost, communityPost)
        .fetchJoin()
        .where(
            comment.user.id.eq(userId),
            comment.communityPost.id.in(postIds),
            comment.isDeleted.eq(false))
        .fetch();
  }
}
