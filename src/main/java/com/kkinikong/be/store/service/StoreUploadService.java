package com.kkinikong.be.store.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.kkinikong.be.store.domain.Store;
import com.kkinikong.be.store.domain.type.Category;
import com.kkinikong.be.store.dto.response.StoreUploadResponse;
import com.kkinikong.be.store.exception.StoreException;
import com.kkinikong.be.store.exception.errorcode.StoreErrorCode;
import com.kkinikong.be.store.repository.storeupload.StoreJdbcRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreUploadService {
  private final StoreJdbcRepository storeJdbcRepository;

  @Transactional
  public StoreUploadResponse upload(MultipartFile file) {

    // CSV 파일을 파싱해서 Store 리스트로 변환
    List<Store> newStores = parseCsv(file);

    // 파일의 첫 번째 데이터에서 지역 정보 추출
    String targetRegion = newStores.get(0).getRegion();

    // 기존 데이터는 유지하며 정보 갱신, 신규 데이터는 추가
    storeJdbcRepository.upsertStores(newStores);

    // 새로운 파일에 없는 가맹점 삭제
    storeJdbcRepository.deleteMissingStores(targetRegion);

    return new StoreUploadResponse(newStores.size(), newStores.size());
  }

  // CSV 파일을 읽어서 Store 객체 리스트로 변환
  private List<Store> parseCsv(MultipartFile file) {
    List<Store> stores = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
      CSVParser csvParser =
          CSVFormat.DEFAULT
              .withFirstRecordAsHeader() // 첫 번째 줄은 헤더
              .withIgnoreHeaderCase() // 대소문자 무시
              .withTrim() // 공백 제거
              .parse(reader);

      for (CSVRecord record : csvParser) {
        stores.add(toStore(record));
      }
    } catch (IOException e) {
      throw new StoreException(StoreErrorCode.CSV_PARSE_FAILED);
    }
    return stores;
  }

  // CSV 한 줄을 Store 객체로 변환
  private Store toStore(CSVRecord record) {
    return Store.builder()
        .name(record.get(0).trim())
        .address(record.get(1).trim())
        .region(extractRegion(record.get(1).trim()))
        .latitude(Double.parseDouble(record.get(2).trim()))
        .longitude(Double.parseDouble(record.get(3).trim()))
        .updatedDate(LocalDate.parse(record.get(4).trim()))
        .category(Category.valueOf(record.get(5).trim()))
        .build();
  }

  // 주소에서 "시 구" 부분만 추출
  private String extractRegion(String address) {
    String[] parts = address.split(" ");
    if (parts.length < 2) {
      throw new StoreException(StoreErrorCode.INVALID_ADDRESS_FORMAT);
    }
    return parts[0] + " " + parts[1];
  }
}
