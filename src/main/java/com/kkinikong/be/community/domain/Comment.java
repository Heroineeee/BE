package com.kkinikong.be.community.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.user.domain.User;

@Table(name = "comments")
@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false, length = 4000)
  private String content;

  @Column(name = "like_count", nullable = false)
  private long likeCount = 0L;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false;

  @Column(name = "is_modified", nullable = false)
  private boolean isModified = false;

  @Column(name = "is_author", nullable = false)
  private boolean isAuthor = false;

  @Column(name = "report_count", nullable = false)
  private long reportCount = 0L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_comment_id")
  private Comment parentComment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_post_id", nullable = false)
  private CommunityPost communityPost;

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentLike> commentLikeList = new ArrayList<>();

  @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> childComments = new ArrayList<>();

  @Builder
  public Comment(
      String content,
      User user,
      CommunityPost communityPost,
      Comment parentComment,
      boolean isAuthor) {
    this.content = content;
    this.user = user;
    this.communityPost = communityPost;
    this.parentComment = parentComment;
    this.isAuthor = isAuthor;
  }

  public void incrementLikeCount() {
    this.likeCount++;
  }

  public void decrementLikeCount() {
    this.likeCount--;
  }

  public void updateIsDeleted() {
    this.isDeleted = true;
  }

  public void update(String content) {
    this.content = content;
    this.isModified = true;
  }

  public void incrementReportCount() {
    this.reportCount++;
  }
}
