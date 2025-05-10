package com.kkinikong.be.store.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.repository.storeupload.StoreJdbcRepository;

@Service
@RequiredArgsConstructor
public class StoreUploadService {
  private final StoreJdbcRepository storeJdbcRepository;

  @Transactional
  public void upload(MultipartFile file) {
    // CSV 파일을 파싱해서 Store 리스트로 변환
    List<Store> stores = parseCsv(file);

    // name|address 조합 key 생성
    List<String> storeKeys =
        stores.stream()
            .map(store -> generateStoreKey(store.getName(), store.getAddress()))
            .collect(Collectors.toList());

    // 이미 존재하는 가맹점 key 조회
    List<String> existingKeys = storeJdbcRepository.findExistingStoreKeys(storeKeys);

    // 중복 제외하고 새로운 Store만 추출
    List<Store> newStores =
        stores.stream()
            .filter(
                store ->
                    !existingKeys.contains(generateStoreKey(store.getName(), store.getAddress())))
            .collect(Collectors.toList());

    // 새로운 Store만 Batch Insert
    if (!newStores.isEmpty()) {
      storeJdbcRepository.saveAllByJdbcTemplate(newStores);
    }
  }

  /// CSV 파일을 읽어서 Store 객체 리스트로 변환
  private List<Store> parseCsv(MultipartFile file) {
    List<Store> stores = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String line;
      boolean isFirstLine = true;
      while ((line = reader.readLine()) != null) {
        if (isFirstLine) {
          isFirstLine = false; // 첫 번째 헤더 -> 스킵
          continue;
        }
        String[] tokens = line.split(",", -1); // 빈 칸도 허용
        stores.add(toStore(tokens));
      }
    } catch (IOException e) {
      throw new StoreException(StoreErrorCode.CSV_PARSE_FAILED);
    }
    return stores;
  }

  /// CSV 한 줄을 Store 객체로 변환
  private Store toStore(String[] tokens) {
    return Store.builder()
        .name(tokens[0].trim())
        .address(tokens[1].trim())
        .region(extractRegion(tokens[1].trim()))
        .latitude(Double.parseDouble(tokens[2].trim()))
        .longitude(Double.parseDouble(tokens[3].trim()))
        .updatedDate(LocalDate.parse(tokens[4].trim()))
        .category(Category.valueOf(tokens[5].trim()))
        .build();
  }

  /// 주소에서 "시 구" 부분만 추출
  private String extractRegion(String address) {
    String[] parts = address.split(" ");
    if (parts.length < 2) {
      throw new StoreException(StoreErrorCode.INVALID_ADDRESS_FORMAT);
    }
    return parts[0] + " " + parts[1];
  }

  /// name + address 조합으로 store 고유 key 생성
  private String generateStoreKey(String name, String address) {
    return name.trim() + "|" + address.trim();
  }
}
