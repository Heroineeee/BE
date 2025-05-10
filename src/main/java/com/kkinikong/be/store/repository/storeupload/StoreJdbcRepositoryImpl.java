package com.kkinikong.be.store.repository.storeupload;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

  /// 새로운 가맹점 리스트를 Batch Insert
  @Override
  @Transactional
  public void saveAllByJdbcTemplate(List<Store> stores) {
    String sql =
        "INSERT INTO stores "
            + "(name, region, category, address, latitude, longitude, rating_avg, scrap_count, review_count, view_count, updated_date) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    jdbcTemplate.batchUpdate(
        sql,
        stores,
        1000,
        (ps, store) -> {
          ps.setString(1, store.getName());
          ps.setString(2, store.getRegion());
          ps.setString(3, store.getCategory().name());
          ps.setString(4, store.getAddress());
          ps.setDouble(5, store.getLatitude());
          ps.setDouble(6, store.getLongitude());
          ps.setDouble(7, 0.0); // 초기값 ratingAvg
          ps.setLong(8, 0L); // 초기값 scrapCount
          ps.setLong(9, 0L); // 초기값 reviewCount
          ps.setLong(10, 0L); // 초기값 viewCount
          ps.setObject(11, store.getUpdatedDate());
        });
  }

  ///  이미 존재하는 가맹점 키를 조회 (name | address)
  @Override
  public List<String> findExistingStoreKeys(List<String> keys) {
    if (keys.isEmpty()) {
      return List.of();
    }

    String sql =
        """
                 SELECT CONCAT(name, '|', address) AS store_key
                 FROM stores
                 WHERE CONCAT(name, '|', address) IN (:keys)
                 """;
    MapSqlParameterSource params = new MapSqlParameterSource("keys", keys);
    return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getString("store_key"));
  }
}
