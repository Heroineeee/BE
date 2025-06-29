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

  public void savePostToSearchIndex(CommunityPost communityPost) {
    elasticsearchOperations.save(CommunityPostDocument.from(communityPost));
  }

  public List<CommunitySearchResponse> searchCommunityPost(String keyword) {
    Query query;
    if (keyword.contains(" ")) { // 띄어쓰기 있는 경우
      String noSpaceKeyword = keyword.replaceAll(" ", "");
      List<String> tokens = Arrays.asList(keyword.split(" "));

      // 레벨 1 : 정확한 구문 일치
      Query level1 =
          MatchPhraseQuery.of(m -> m.field("titleWithContent").query(keyword).boost(100f))
              ._toQuery();

      // 레벨 2 : 띄어쓰기 제거 후 정확한 구문 일치
      Query level2 =
          MatchQuery.of(m -> m.field("titleWithContent").query(noSpaceKeyword).boost(50f))
              ._toQuery();

      // 레벨 3: 토큰 포함 여부
      Query level3 =
          BoolQuery.of(
                  b ->
                      b.should(
                              tokens.stream()
                                  .map(
                                      token ->
                                          MatchQuery.of(
                                                  m -> m.field("titleWithContent").query(token))
                                              ._toQuery())
                                  .toList())
                          .minimumShouldMatch(String.valueOf(tokens.size()))
                          .boost(10f))
              ._toQuery();

      query = BoolQuery.of(b -> b.should(level1).should(level2).should(level3))._toQuery();
    } else { // 띄어쓰기 없는 경우
      query = MatchQuery.of(m -> m.field("titleWithContent").query(keyword))._toQuery();
    }

    try {
      SearchResponse<CommunityPostDocument> searchResponse =
          elasticsearchClient.search(
              s -> s.index("community_post").query(query).size(20), CommunityPostDocument.class);
      System.out.println(
          "Elasticsearch 검색 결과: "
              + searchResponse.hits().hits().size()
              + "개, 쿼리: "
              + query.toString());

      return searchResponse.hits().hits().stream()
          .peek(
              hit -> {
                double score = hit.score();
                CommunityPostDocument doc = hit.source();
                System.out.println(
                    "Search Result: id="
                        + doc.getId()
                        + ", combined="
                        + doc.getTitleWithContent()
                        + ", score="
                        + score);
              })
          .map(
              hit -> {
                CommunityPostDocument doc = hit.source();
                return new CommunitySearchResponse(doc.getId(), doc.getTitleWithContent());
              })
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Elasticsearch 검색 오류: " + e.getMessage(), e);
    }
  }
}
