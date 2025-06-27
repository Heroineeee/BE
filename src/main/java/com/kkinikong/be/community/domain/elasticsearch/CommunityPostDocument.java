package com.kkinikong.be.community.domain.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
@Document(indexName = "community_post")
public class CommunityPostDocument {
  @Id private Long id;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String content;

  public static CommunityPostDocument from(CommunityPost post) {
    return CommunityPostDocument.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .build();
  }
}
