package com.kkinikong.be.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.community.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}
