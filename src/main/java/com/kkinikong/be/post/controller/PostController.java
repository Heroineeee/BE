package com.kkinikong.be.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.global.response.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "Post", description = "Post 관련 API")
public class PostController {

  @GetMapping("/test")
  public ResponseEntity<ApiResponse<?>> getTest() {
    // service.getTest();
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from("test"));
  }
}
