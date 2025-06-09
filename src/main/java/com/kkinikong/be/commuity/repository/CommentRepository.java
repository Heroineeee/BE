package com.kkinikong.be.commuity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kkinikong.be.commuity.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}
