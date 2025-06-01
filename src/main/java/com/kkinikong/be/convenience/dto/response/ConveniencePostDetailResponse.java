package com.kkinikong.be.convenience.dto.response;

import java.time.LocalDate;

import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;

public record ConveniencePostDetailResponse(
    String userName,
    boolean isMine, // true: 본인 게시글, false: 타인 게시글
    LocalDate createTime,
    String name,
    boolean isAvailable,
    Brand brand,
    Category category,
    String description,
    long CorrectCount,
    long IncorrectCount) {}
