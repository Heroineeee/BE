package com.kkinikong.be.community.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.global.entity.BaseEntity;

@Table(name = "community_post_images")
@Entity
@Getter
@NoArgsConstructor
public class CommunityPostImage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "community_post_id", nullable = false)
  private CommunityPost communityPost;

  @Builder
  public CommunityPostImage(String imageUrl, CommunityPost communityPost) {
    this.imageUrl = imageUrl;
    this.communityPost = communityPost;
  }
}
