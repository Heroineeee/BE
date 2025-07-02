package com.kkinikong.be.community.domain.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kkinikong.be.community.domain.CommunityPost;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPostDocument {
  @Id private Long id;

  private String titleWithContent;

  public static CommunityPostDocument from(CommunityPost post) {
    return CommunityPostDocument.builder()
        .id(post.getId())
        .titleWithContent(post.getTitle() + " " + post.getContent())
        .build();
  }
}
