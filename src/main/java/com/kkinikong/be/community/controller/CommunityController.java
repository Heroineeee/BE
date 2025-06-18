package com.kkinikong.be.community.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.dto.request.CommunityCommentRequest;
import com.kkinikong.be.community.dto.request.CommunityPostRequest;
import com.kkinikong.be.community.dto.response.CommunityPostResponse;
import com.kkinikong.be.community.exception.CommunityException;
import com.kkinikong.be.community.exception.errorcode.CommunityErrorCode;
import com.kkinikong.be.community.service.CommunityService;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Community", description = "커뮤니티 관련 API")
@RequestMapping("/api/v1/community")
public class CommunityController {

  private final int MAX_COMMENT_SIZE = 4000;
  private final int MAX_REPLY_SIZE = 2000;

  private final CommunityService communityService;

  @PostMapping("/post")
  @Operation(
      summary = "커뮤니티 게시글 작성",
      description = "커뮤니티 게시글을 작성하는 API입니다. 제목은 5자 이상, 내용은 10자 이상이어야 하며, 카테고리는 필수입니다.")
  public ResponseEntity<ApiResponse<Object>> postCommunityPost(
      @RequestBody @Valid CommunityPostRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    CommunityPostResponse communityPostResponse =
        communityService.postCommunityPost(request, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(communityPostResponse));
  }

  @Operation(
      summary = "커뮤니티 게시물 작성 시 사진 추가",
      description =
          """
           - 커뮤니티 게시글 작성 api를 통해 id를 받은 후, 해당 id로 사진을 추가합니다.
           - 사진이 없는 경우 file 비워서 보내셔도 되고 아예 호출 안하셔도 됩니다.
           - 사진은 최대 3장까지 전송 가능하며 각 10MB, 총 30MB 이하로 제한됩니다.
           - 가능한 파일 확장자는 .jpg, .jpeg, .png, .heic 입니다.
           """)
  @PostMapping(path = "/post/{postId}/image", consumes = "multipart/form-data")
  public ResponseEntity<ApiResponse<Object>> postCommunityPostImage(
      @PathVariable("postId") Long postId,
      @Parameter(
              description = "업로드할 파일 리스트",
              content = @Content(mediaType = "application/octet-stream"))
          @RequestParam(value = "files", required = false)
          List<MultipartFile> files,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    communityService.postCommunityPostImage(postId, files, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @PostMapping("/post/{postId}/comment")
  @Operation(
      summary = "커뮤니티 게시글에 댓글 작성",
      description = "커뮤니티 게시글에 댓글을 작성하는 API입니다. 댓글 내용은 공백일 수 없으며, 4000자 이하여야 합니다.")
  public ResponseEntity<ApiResponse<Object>> postCommunityComment(
      @PathVariable("postId") Long postId,
      @RequestBody @Valid CommunityCommentRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    if (request.content().length() > MAX_COMMENT_SIZE) {
      throw new CommunityException(CommunityErrorCode.COMMENT_SIZE_LIMIT);
    }
    communityService.postCommentAndReply(postId, null, request, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }

  @PostMapping("/post/{postId}/comment/{commentId}/reply")
  @Operation(
      summary = "커뮤니티 게시글 댓글에 답글 작성",
      description =
          "커뮤니티 게시글 댓글의 답글을 작성하는 API입니다. 답글 내용은 공백일 수 없으며, 2000자 이하여야 합니다."
              + "답글은 최상위 댓글에만 작성할 수 있습니다.")
  public ResponseEntity<ApiResponse<Object>> postCommunityReply(
      @PathVariable("postId") Long postId,
      @PathVariable("commentId") Long commentId,
      @RequestBody @Valid CommunityCommentRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    if (request.content().length() > MAX_REPLY_SIZE) {
      throw new CommunityException(CommunityErrorCode.REPLY_SIZE_LIMIT);
    }

    communityService.postCommentAndReply(postId, commentId, request, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }

  @GetMapping("/post/{postId}")
  @Operation(
      summary = "커뮤니티 게시글 상세 조회",
      description = "커뮤니티 게시글을 상세 조회하는 API입니다. 게시글 ID를 통해 해당 게시글과 댓글, 답글을 조회합니다.")
  public ResponseEntity<ApiResponse<Object>> getCommunityPost(@PathVariable("postId") Long postId) {

    // CommunityPostDetailResponse communityPostResponse =
    //     communityService.getCommunityPost(postId, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }

  @GetMapping("/post")
  @Operation(
      summary = "커뮤니티 게시글 목록 조회 및 필터링",
      description = "커뮤니티 게시글 목록을 조회하는 API입니다. 카테고리와 페이지 정보를 통해 게시글을 조회합니다.")
  public ResponseEntity<ApiResponse<Object>> getCommunityPostList() {
    // communityService.getCommunityPostList();
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }

  @GetMapping("/post/popular")
  @Operation(
      summary = "인기 커뮤니티 게시글 조회",
      description = "인기 커뮤니티 게시글을 조회하는 API입니다. 인기 게시글은 조회수가 높은 순으로 정렬됩니다.")
  public ResponseEntity<ApiResponse<Object>> getPopularCommunityPosts() {
    // communityService.getPopularCommunityPosts();
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }
}
