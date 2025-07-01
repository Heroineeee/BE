package com.kkinikong.be.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SearchConfig {
  @Value("${OPEN_SEARCH_HOST}")
  private String opensearchHost;

  @Value("${OPEN_SEARCH_PORT}")
  private int opensearchPort;

  @Value("${OPEN_SEARCH_USER}")
  private String opensearchUser;

  @Value("${OPEN_SEARCH_PASSWORD}")
  private String opensearchPassword;

  @Bean
  public OpenSearchClient openSearchClient() {
    return new OpenSearchClient(
        new RestClientTransport(restClient(), new JacksonJsonpMapper(new ObjectMapper())));
  }

  @Bean
  public RestClient restClient() {
    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(opensearchUser, opensearchPassword));

    return RestClient.builder(new HttpHost(opensearchHost, opensearchPort, "https"))
        .setHttpClientConfigCallback(
            httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
        .build();
  }
}
