package com.kkinikong.be.store.repository.storeupload;

import java.time.LocalDate;
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

  /// 새로운 가맹점 리스트를 Batch Insert
  @Override
  @Transactional
  public void saveAllByJdbcTemplate(List<Store> stores) {
    String sql =
        "INSERT INTO stores "
            + "(name, region, category, address, latitude, longitude, "
            + "rating_avg, scrap_count, review_count, view_count, updated_date, created_date, modified_date) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
          ps.setDouble(7, 0.0);
          ps.setLong(8, 0L);
          ps.setLong(9, 0L);
          ps.setLong(10, 0L);
          ps.setObject(11, store.getUpdatedDate());
          ps.setObject(12, LocalDate.now());
          ps.setObject(13, LocalDate.now());
        });
  }

  @Override
  @Transactional
  public void deleteByRegion(String region) {
    // 해당 지역의 모든 가맹점 삭제
    String sql = "DELETE FROM stores WHERE region = ?";
    jdbcTemplate.update(sql, region);
  }
}
