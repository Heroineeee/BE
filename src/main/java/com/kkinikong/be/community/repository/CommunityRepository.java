package com.kkinikong.be.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.CommunityPost;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {}
