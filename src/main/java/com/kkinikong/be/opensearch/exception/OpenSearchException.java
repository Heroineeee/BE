package com.kkinikong.be.opensearch.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.opensearch.exception.errorcode.OpenSearchErrorCode;

@Getter
@RequiredArgsConstructor
public class OpenSearchException extends RuntimeException {
  private final OpenSearchErrorCode errorCode;
}
