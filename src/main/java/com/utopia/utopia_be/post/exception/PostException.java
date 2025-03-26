package com.utopia.utopia_be.post.exception;

import com.utopia.utopia_be.global.exception.errorcode.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException {
	private final ErrorCode errorCode;
}
