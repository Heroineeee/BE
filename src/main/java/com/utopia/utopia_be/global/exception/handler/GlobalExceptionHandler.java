package com.utopia.utopia_be.global.exception.handler;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.micrometer.common.lang.NonNull;

import com.utopia.utopia_be.global.exception.errorcode.ErrorCode;
import com.utopia.utopia_be.global.exception.errorcode.GlobalErrorCode;
import com.utopia.utopia_be.global.exception.response.ErrorResponse;
import com.utopia.utopia_be.global.exception.response.ErrorResponse.ValidationError;
import com.utopia.utopia_be.global.exception.response.ErrorResponse.ValidationErrors;
import com.utopia.utopia_be.post.exception.PostException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * 커스텀 예외 코드 예시 @ExceptionHandler(UserNotFoundException.class) public ResponseEntity<Object>
   * handleMemberNotFound(final UserNotFoundException e) { return
   * handleExceptionInternal(e.getErrorCode()); }
   */
  @ExceptionHandler(PostException.class)
  public ResponseEntity<Object> handlePostException(final PostException e) {
    return handleExceptionInternal(e.getErrorCode());
  }

  /**
   * @Valid 관련 예외 처리 (DTO 검증 실패 시 발생)
   */
  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException e,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    return handleExceptionInternal(e);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
    return handleExceptionInternal(GlobalErrorCode.INVALID_PARAMETER);
  }

  /** 모든 예외를 처리하는 기본 예외 처리기 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllException(Exception e, WebRequest request) {
    return handleExceptionInternal(GlobalErrorCode.INTERNAL_SERVER_ERROR);
  }

  /** 예외 처리 결과를 생성하는 내부 메서드 */
  private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
    return ResponseEntity.status(errorCode.getHttpStatus()).body(makeErrorResponse(errorCode));
  }

  /** ErrorResponse 객체를 생성하는 메서드 */
  private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
    return ErrorResponse.builder()
        .isSuccess(false)
        .code(errorCode.name())
        .message(errorCode.getMessage())
        .results(new ValidationErrors(null))
        .build();
  }

  /** BindException (DTO 검증 실패) 처리 */
  private ResponseEntity<Object> handleExceptionInternal(BindException e) {
    return ResponseEntity.status(GlobalErrorCode.INVALID_PARAMETER.getHttpStatus())
        .body(makeErrorResponse(e));
  }

  /** BindException에서 발생한 유효성 오류를 ErrorResponse로 변환 */
  private ErrorResponse makeErrorResponse(BindException e) {
    final List<ValidationError> validationErrorList =
        e.getBindingResult().getFieldErrors().stream()
            .map(ErrorResponse.ValidationError::from)
            .toList();

    return ErrorResponse.builder()
        .isSuccess(false)
        .code(GlobalErrorCode.INVALID_PARAMETER.name())
        .message(GlobalErrorCode.INVALID_PARAMETER.getMessage())
        .results(new ValidationErrors(validationErrorList))
        .build();
  }
}
