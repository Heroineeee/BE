package com.kkinikong.be.convenience.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;

public interface ConvenienceRepositoryCustom {
  Page<ConveniencePost> findByCondition(
      String keyword, Brand brand, Category category, Boolean isAvailableCheck, Pageable pageable);
}
