package com.kkinikong.be.convenience.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.convenience.domain.ConveniencePost;

@Repository
public interface ConvenienceRepository extends JpaRepository<ConveniencePost, Long> {}
