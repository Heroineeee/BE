package com.kkinikong.be.store.repository.storeupload;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.store.domain.Store;

@Repository
@RequiredArgsConstructor
public class StoreJdbcRepositoryImpl implements StoreJdbcRepository {
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private static final int BATCH_SIZE = 1000;

  @Override
  @Transactional
  public void upsertStores(List<Store> stores) {
    String sql =
        "INSERT INTO stores "
            + "(name, region, category, address, latitude, longitude, "
            + "rating_avg, scrap_count, review_count, view_count, updated_date, created_date, modified_date) "
            + "VALUES (?, ?, ?, ?, ?, ?, 0.0, 0, 0, 0, ?, NOW(), NOW()) "
            + "ON DUPLICATE KEY UPDATE "
            + "category = VALUES(category), "
            + "latitude = VALUES(latitude), "
            + "longitude = VALUES(longitude), "
            + "updated_date = VALUES(updated_date), "
            + "modified_date = NOW()";

    jdbcTemplate.batchUpdate(
        sql,
        stores,
        BATCH_SIZE,
        (ps, store) -> {
          ps.setString(1, store.getName());
          ps.setString(2, store.getRegion());
          ps.setString(3, store.getCategory().name());
          ps.setString(4, store.getAddress());
          ps.setDouble(5, store.getLatitude());
          ps.setDouble(6, store.getLongitude());
          ps.setObject(7, store.getUpdatedDate());
        });
  }

  @Override
  @Transactional
  public void deleteMissingStores(String region, List<String> currentStoreNames) {
    if (currentStoreNames.isEmpty()) return;

    // 해당 지역 데이터 중 이번 CSV 파일에 없는 가맹점만 삭제
    String sql = "DELETE FROM stores WHERE region = :region AND name NOT IN (:names)";

    var params =
        new org.springframework.jdbc.core.namedparam.MapSqlParameterSource()
            .addValue("region", region)
            .addValue("names", currentStoreNames);

    namedParameterJdbcTemplate.update(sql, params);
  }
}
