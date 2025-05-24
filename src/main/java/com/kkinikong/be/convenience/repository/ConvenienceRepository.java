package com.kkinikong.be.convenience.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.convenience.domain.ConveniencePost;

public interface ConvenienceRepository extends JpaRepository<ConveniencePost, Long> {}
