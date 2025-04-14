package com.kkinikong.be.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkinikong.be.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findById(Long id);
}
