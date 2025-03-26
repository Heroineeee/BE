package com.utopia.utopia_be.post.service;

import static com.utopia.utopia_be.post.exception.errorcode.PostErrorCode.POST_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.utopia.utopia_be.post.exception.PostException;
import com.utopia.utopia_be.post.repository.PostRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;

  @Transactional
  public void getPost(Long id) {
    postRepository.findById(id).orElseThrow(() -> new PostException(POST_NOT_FOUND));
  }
}
