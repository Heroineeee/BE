package com.kkinikong.be.community.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.elasticsearch.CommunityPostDocument;
import com.kkinikong.be.community.dto.response.CommunitySearchResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunitySearchService {
  private final ElasticsearchClient elasticsearchClient;

  private final ElasticsearchOperations elasticsearchOperations;

  @Transactional(readOnly = true)
  public void savePostToSearchIndex(CommunityPost communityPost) {
    elasticsearchOperations.save(CommunityPostDocument.from(communityPost));
  }

  public List<CommunitySearchResponse> searchCommunityPost(String keyword) {
    Query query;

    if (keyword.contains(" ")) { // 띄어쓰기 있는 경우
      String noSpaceKeyword = keyword.replaceAll(" ", "");
      List<String> tokens = Arrays.asList(keyword.split(" "));

      query =
          BoolQuery.of(
                  b ->
                      b
                          // 레벨 1 : 정확히 포함
                          .should(
                              MatchPhraseQuery.of(m -> m.field("title").query(keyword).boost(5.0f))
                                  ._toQuery())
                          .should(
                              MatchPhraseQuery.of(
                                      m -> m.field("content").query(keyword).boost(5.0f))
                                  ._toQuery())
                          // 레벨 2 : 띄어쓰기 제거한 키워드 포함
                          .should(
                              MatchQuery.of(m -> m.field("title").query(noSpaceKeyword).boost(2.0f))
                                  ._toQuery())
                          .should(
                              MatchQuery.of(
                                      m -> m.field("content").query(noSpaceKeyword).boost(2.0f))
                                  ._toQuery())
                          // 레벨 3 : 각 토큰을 포함하는지 검색
                          .should(
                              qb ->
                                  qb.bool(
                                      inner ->
                                          inner.should(
                                              tokens.stream()
                                                  .map(
                                                      token ->
                                                          MatchQuery.of(
                                                                  m ->
                                                                      m.field("title")
                                                                          .query(token)
                                                                          .boost(1.0f))
                                                              ._toQuery())
                                                  .toList())))
                          .should(
                              qb ->
                                  qb.bool(
                                      inner ->
                                          inner.should(
                                              tokens.stream()
                                                  .map(
                                                      token ->
                                                          MatchQuery.of(
                                                                  m ->
                                                                      m.field("content")
                                                                          .query(token)
                                                                          .boost(1.0f))
                                                              ._toQuery())
                                                  .toList()))))
              ._toQuery();

    } else { // 띄어쓰기 없는 경우
      query =
          BoolQuery.of(
                  b ->
                      b.should(
                              WildcardQuery.of(
                                      w -> w.field("title.keyword").value("*" + keyword + "*"))
                                  ._toQuery())
                          .should(
                              WildcardQuery.of(
                                      w -> w.field("content.keyword").value("*" + keyword + "*"))
                                  ._toQuery()))
              ._toQuery();
    }

    try {
      SearchResponse<CommunityPostDocument> searchResponse =
          elasticsearchClient.search(
              s -> s.index("community_post").query(query).size(20), CommunityPostDocument.class);

      return searchResponse.hits().hits().stream()
          .map(
              hit -> {
                CommunityPostDocument doc = hit.source();
                return new CommunitySearchResponse(doc.getId(), doc.getTitle(), doc.getContent());
              })
          .collect(Collectors.toList());

    } catch (Exception e) {
      throw new RuntimeException("Elasticsearch 검색 오류: " + e.getMessage(), e);
    }
  }
}
