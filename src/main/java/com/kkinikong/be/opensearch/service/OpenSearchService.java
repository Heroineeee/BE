package com.kkinikong.be.opensearch.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchPhraseQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;

import com.kkinikong.be.community.domain.document.CommunityPostDocument;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenSearchService {

  private final OpenSearchClient openSearchClient;

  @Value("${opensearch.index}")
  private String indexName;

  // 최초 1회 인덱스 생성
  public void createIndexIfNotExists() {
    try {
      boolean exists = openSearchClient.indices().exists(b -> b.index(indexName)).value();
      if (!exists) {
        CreateIndexRequest createIndexRequest =
            new CreateIndexRequest.Builder()
                .index(indexName)
                .settings(
                    s ->
                        s.analysis(
                            a ->
                                a.analyzer(
                                    "nori_analyzer",
                                    analyzer ->
                                        analyzer.custom(c -> c.tokenizer("nori_tokenizer")))))
                .mappings(
                    m ->
                        m.properties("id", p -> p.long_(l -> l))
                            .properties(
                                "titleWithContent",
                                p ->
                                    p.text(
                                        t ->
                                            t.analyzer("nori_analyzer")
                                                .fields(
                                                    "keyword",
                                                    k -> k.keyword(kf -> kf.ignoreAbove(256))))))
                .build();
        openSearchClient.indices().create(createIndexRequest);
      }

    } catch (IOException e) {
      throw new CommunityException(CommunityErrorCode.FAILED_TO_SAVE_INDEX);
    }
  }

  public void resetIndex(String index) {
    try {
      boolean exists = openSearchClient.indices().exists(b -> b.index(index)).value();
      if (exists) {
        openSearchClient.indices().delete(d -> d.index(index));
      }

    } catch (IOException e) {
      throw new CommunityException(CommunityErrorCode.FAILED_TO_RESET_INDEX);
    }
  }

  public void savePostToSearchIndex(CommunityPostDocument communityPostDocument) {
    try {
      openSearchClient.index(
          IndexRequest.of(
              builder ->
                  builder
                      .index(indexName)
                      .id(String.valueOf(communityPostDocument.getId()))
                      .document(communityPostDocument)
                      .refresh(Refresh.True)));
    } catch (Exception e) {
      throw new CommunityException(CommunityErrorCode.FAILED_TO_SAVE_INDEX);
    }
  }

  public List<Long> searchCommunityPost(String keyword, int page, int size) {
    try {
      Query query = buildSearchQuery(keyword);

      SearchRequest request =
          SearchRequest.of(
              searchRequest ->
                  searchRequest
                      .index(indexName)
                      .from(page * size)
                      .size(size)
                      .query(query)
                      .source(src -> src.filter(f -> f.includes(List.of()))));

      SearchResponse<Void> searchResponse = openSearchClient.search(request, Void.class);

      return searchResponse.hits().hits().stream().map(Hit::id).map(Long::parseLong).toList();

    } catch (IOException e) {
      throw new CommunityException(CommunityErrorCode.FAILED_TO_SEARCH_INDEX);
    }
  }

  public void deletePostFromSearchIndex(Long postId) {
    try {
      openSearchClient.delete(
          d -> d.index(indexName).id(String.valueOf(postId)).refresh(Refresh.True));
    } catch (IOException e) {
      throw new CommunityException(CommunityErrorCode.FAILED_TO_DELETE_INDEX);
    }
  }

  private static Query buildSearchQuery(String keyword) {
    // 키워드가 공백을 포함하는 경우
    if (keyword.contains(" ")) {
      String noSpaceKeyword = keyword.replaceAll(" ", "");
      List<String> tokens = Arrays.asList(keyword.split(" "));

      // 레벨 1 : 정확한 구문 일치
      Query level1 =
          MatchPhraseQuery.of(m -> m.field("titleWithContent").query(keyword).boost(100f))
              ._toQuery();

      // 레벨 2 : 공백 제거 후 일치
      Query level2 =
          MatchQuery.of(
                  m ->
                      m.field("titleWithContent")
                          .query(FieldValue.of(noSpaceKeyword))
                          .fuzziness("1")
                          .boost(50f))
              ._toQuery();

      // 레벨 3 : 각 토큰에 대해 개별적으로 일치
      Query level3 =
          BoolQuery.of(
                  b ->
                      b.should(
                              tokens.stream()
                                  .map(
                                      token ->
                                          MatchQuery.of(
                                                  m ->
                                                      m.field("titleWithContent")
                                                          .query(FieldValue.of(token))
                                                          .fuzziness("1"))
                                              ._toQuery())
                                  .toList())
                          .minimumShouldMatch("1")
                          .boost(10f))
              ._toQuery();

      return BoolQuery.of(b -> b.should(level1).should(level2).should(level3))._toQuery();

    } else { // 키워드가 공백을 포함하지 않는 경우
      return MatchQuery.of(
              m -> m.field("titleWithContent").query(FieldValue.of(keyword)).fuzziness("1"))
          ._toQuery();
    }
  }
}
