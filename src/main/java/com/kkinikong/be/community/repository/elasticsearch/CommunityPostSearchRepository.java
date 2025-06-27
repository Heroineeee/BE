package com.kkinikong.be.community.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.elasticsearch.CommunityPostDocument;

@Repository
public interface CommunityPostSearchRepository
    extends ElasticsearchRepository<CommunityPostDocument, Long> {}
