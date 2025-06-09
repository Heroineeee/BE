package com.kkinikong.be.community.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Community", description = "커뮤니티 관련 API")
@RequestMapping("/api/v1/community")
public class CommunityController {}
