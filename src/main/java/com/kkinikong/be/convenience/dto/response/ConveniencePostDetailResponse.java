package com.kkinikong.be.convenience.dto.response;

import java.time.LocalDate;

import com.kkinikong.be.convenience.domain.ConveniencePost;
import com.kkinikong.be.convenience.domain.type.Brand;
import com.kkinikong.be.convenience.domain.type.Category;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.utils.UserNicknameUtil;

public record ConveniencePostDetailResponse(
    String userNickname,
    boolean isMine, // true: 본인 게시글, false: 타인 게시글
    LocalDate createTime,
    String name,
    boolean isAvailable,
    Brand brand,
    Category category,
    String description,
    long CorrectCount,
    long IncorrectCount) {

  public static ConveniencePostDetailResponse from(User user, ConveniencePost conveniencePost) {
    return new ConveniencePostDetailResponse(
        UserNicknameUtil.displayNickname(conveniencePost.getUser()),
        user != null && user.getId().equals(conveniencePost.getUser().getId()),
        conveniencePost.getCreatedDate().toLocalDate(),
        conveniencePost.getName(),
        conveniencePost.getIsAvailable(),
        conveniencePost.getBrand(),
        conveniencePost.getCategory(),
        conveniencePost.getDescription(),
        conveniencePost.getCorrectCount(),
        conveniencePost.getIncorrectCount());
  }
}
