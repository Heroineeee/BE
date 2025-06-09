package com.kkinikong.be.commuity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.commuity.domain.CommentLike;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {}
