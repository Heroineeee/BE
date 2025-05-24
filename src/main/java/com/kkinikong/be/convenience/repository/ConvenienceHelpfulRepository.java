package com.kkinikong.be.convenience.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.convenience.domain.ConvenienceHelpful;

@Repository
public interface ConvenienceHelpfulRepository extends JpaRepository<ConvenienceHelpful, Long> {}
