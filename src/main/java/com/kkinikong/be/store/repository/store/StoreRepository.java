package com.kkinikong.be.store.repository.store;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;

import com.kkinikong.be.store.domain.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
  Optional<Store> findStoreById(Long id);

  List<Store> findByRegion(String region);

  @Query("SELECT s.id FROM Store s WHERE s.region = :region")
  List<Long> findIdsByRegion(@Param("region") String region);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Store s SET s.viewCount = s.viewCount + :count WHERE s.id = :storeId")
  void incrementViews(@Param("storeId") Long storeId, @Param("count") Long count);

  @Query(
      value =
          """
    SELECT COUNT(DISTINCT city_name) AS city_count
    FROM (
      SELECT
        CASE
          WHEN region LIKE '서울%' OR region LIKE '인천%' THEN NULL
          WHEN region LIKE '%광역시%' THEN SPLIT_PART(TRIM(region), ' ', 1)
          WHEN region LIKE '%시' THEN REVERSE(SPLIT_PART(REVERSE(TRIM(region)), ' ', 1))
          ELSE NULL
        END AS city_name
      FROM stores
    ) t
    WHERE city_name IS NOT NULL
    """,
      nativeQuery = true)
  long getStoreRegionCount();
}
