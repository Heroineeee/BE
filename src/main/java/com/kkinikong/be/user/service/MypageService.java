package com.kkinikong.be.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.PageResponse;
import com.kkinikong.be.store.domain.StoreScrap;
import com.kkinikong.be.store.dto.response.StoreMapListItemResponse;
import com.kkinikong.be.store.repository.storescrap.StoreScrapRepository;

@Service
@RequiredArgsConstructor
public class MypageService {
  private final StoreScrapRepository storeScrapRepository;

  public PageResponse<StoreMapListItemResponse> getScrapStore(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<StoreScrap> storeScrapPage = storeScrapRepository.findAllByUserId(userId, pageable);
    return PageResponse.from(
        storeScrapPage, scrap -> StoreMapListItemResponse.from(scrap.getStore()));
  }
}
