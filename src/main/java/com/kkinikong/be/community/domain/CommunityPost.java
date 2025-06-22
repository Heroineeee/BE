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

import com.kkinikong.be.community.domain.type.Category;
import com.kkinikong.be.global.entity.BaseEntity;
import com.kkinikong.be.user.domain.User;

@Table(name = "community_posts")
@Entity
@Getter
@NoArgsConstructor
public class CommunityPost extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "category", nullable = false)
  private Category category;

  @Column(name = "view_count", nullable = false)
  private long viewCount = 0L;

  @Column(name = "like_count", nullable = false)
  private long likeCount = 0L;

  @Column(name = "comment_count", nullable = false)
  private long commentCount = 0L;

  @Column(name = "thumbnail_url")
  private String thumbnailUrl;

  @Column(name = "is_modified", nullable = false)
  private boolean isModified = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "communityPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommunityPostImage> communityPostImageList = new ArrayList<>();

  @OneToMany(mappedBy = "communityPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommunityPostLike> communityPostLikeList = new ArrayList<>();

  @OneToMany(mappedBy = "communityPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> commentList = new ArrayList<>();

  @Builder
  public CommunityPost(String title, String content, User user, Category category) {
    this.title = title;
    this.content = content;
    this.user = user;
    this.category = category;
  }

  public void incrementCommentCount() {
    this.commentCount++;
  }

  public void updateThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public void incrementLikeCount() {
    this.likeCount++;
  }

  public void decrementLikeCount() {
    this.likeCount--;
  }
}
