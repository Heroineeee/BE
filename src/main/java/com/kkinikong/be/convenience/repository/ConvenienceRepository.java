package com.kkinikong.be.convenience.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.domain.type.Brand;

@Repository
public interface ConvenienceRepository
    extends JpaRepository<ConveniencePost, Long>, ConvenienceRepositoryCustom {

  Page<ConveniencePost> findAllByBrand(Brand brand, Pageable pageable);
}
