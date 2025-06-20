package com.kkinikong.be.community.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.domain.type.Category;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

  List<CommunityPost> findTop5ByOrderByLikeCountDescViewCountDesc();

  Page<CommunityPost> findAllByCategory(Category category, Pageable pageable);

  Page<CommunityPost> findAll(Pageable pageable);
}
