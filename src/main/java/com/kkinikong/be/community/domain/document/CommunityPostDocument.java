package com.kkinikong.be.community.domain.document;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunityPostDocument {
  @Id private Long id;

  @Field(name = "titleWithContent", type = FieldType.Text, analyzer = "nori")
  private String titleWithContent;

  public static CommunityPostDocument from(CommunityPost post) {
    return CommunityPostDocument.builder()
        .id(post.getId())
        .titleWithContent(post.getTitle() + " " + post.getContent())
        .build();
  }
}
