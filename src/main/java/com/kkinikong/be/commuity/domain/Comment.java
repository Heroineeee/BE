package com.kkinikong.be.commuity.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;

@Table(name = "comments")
@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "like_count", nullable = false)
  private long likeCount = 0L;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "community_post_id", nullable = false)
  private Long communityPostId;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false;

  @Column(name = "parent_comment_id")
  private Long parentCommentId;

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentLike> commentLikeList = new ArrayList<>();

  @Builder
  public Comment(String content, Long userId, Long communityPostId, Long parentCommentId) {
    this.content = content;
    this.userId = userId;
    this.communityPostId = communityPostId;
    this.parentCommentId = parentCommentId;
  }
}
