package com.utopia.utopia_be.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utopia.utopia_be.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/post")
public class PostController {

	@GetMapping("/test")
	public ResponseEntity<ApiResponse<?>> getTest(){
		//service.getTest();
		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.from("test"));
	}


}
