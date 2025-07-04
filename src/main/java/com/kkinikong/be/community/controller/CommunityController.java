package com.kkinikong.be.community.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import com.kkinikong.be.community.domain.type.Category;
import com.kkinikong.be.community.dto.request.CommunityCommentRequest;
import com.kkinikong.be.community.dto.request.CommunityPostRequest;
import com.kkinikong.be.community.dto.response.CommentResponse;
import com.kkinikong.be.community.dto.response.CommunityPostResponse;
import com.kkinikong.be.community.dto.response.LikeToggleResponse;
import com.kkinikong.be.community.service.CommunityService;
import com.kkinikong.be.global.response.ApiResponse;
import com.kkinikong.be.user.utils.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Tag(name = "Community", description = "커뮤니티 관련 API")
@RequestMapping("/api/v1/community")
public class CommunityController {

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

    CommentResponse commentResponse =
        communityService.postCommentAndReply(postId, null, request, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(commentResponse));
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

    CommentResponse commentResponse =
        communityService.postCommentAndReply(postId, commentId, request, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(commentResponse));
  }

  @GetMapping("/post/{postId}")
  @Operation(
      summary = "커뮤니티 게시글 상세 조회",
      description =
          """
          - 커뮤니티 게시글의 상세 정보와 댓글을 조회하는 API입니다.
          - 게시글 ID를 통해 해당 게시글을 조회하며, 조회 시 게시글의 조회수가 증가합니다.
          - isModified : true/false
          - isLiked, isMyCommunityPost, isMyComment : true/false/null
          - 인증되지 않은 유저의 경우, 위의 필드는 null로 반환됩니다.
          """)
  public ResponseEntity<ApiResponse<Object>> getCommunityPost(
      @PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponse.from(
                communityService.getCommunityPost(
                    postId, userDetails == null ? null : userDetails.getId())));
  }

  @GetMapping("/post")
  @Operation(
      summary = "커뮤니티 게시글 목록 조회 및 카테고리 필터링",
      description =
          """
           - 커뮤니티 게시글 목록을 조회하는 API입니다.
           - 카테고리로 필터링할 수 있으며, 전체 조회 시 category 파라미터를 생략하거나 null로 설정합니다.
           - 최신순으로 정렬되며, 페이지네이션을 지원합니다.
           - 페이지는 0부터 시작하며, 개수는 10개로 기본 설정되어 있습니다.
           """)
  public ResponseEntity<ApiResponse<Object>> getCommunityPostList(
      @RequestParam(value = "category", required = false) Category category,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponse.from(
                ApiResponse.from(communityService.getCommunityPostList(category, page, size))));
  }

  @GetMapping("/post/popular")
  @Operation(
      summary = "인기 커뮤니티 게시글 조회",
      description = "인기 커뮤니티 게시글을 조회하는 API입니다. 인기 게시글은 좋아요 수 기준, 같을 시 조회수 순으로 정렬됩니다.")
  public ResponseEntity<ApiResponse<Object>> getPopularCommunityPosts() {

    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.from(communityService.getPopularCommunityPosts()));
  }

  @PostMapping("/post/{postId}/like")
  @Operation(
      summary = "커뮤니티 게시글 좋아요, 좋아요 취소",
      description = "커뮤니티 게시글에 좋아요를 누르는 API입니다. 이미 좋아요를 누른 경우, 좋아요가 취소됩니다.")
  public ResponseEntity<ApiResponse<Object>> postCommunityPostLike(
      @PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {

    LikeToggleResponse likeToggleResponse =
        communityService.postCommunityPostLike(postId, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(likeToggleResponse));
  }

  @PostMapping("/comment/{commentId}/like")
  @Operation(
      summary = "커뮤니티 게시글 댓글 좋아요, 좋아요 취소",
      description = "커뮤니티 게시글 댓글에 좋아요를 누르는 API입니다. 이미 좋아요를 누른 경우, 좋아요가 취소됩니다.")
  public ResponseEntity<ApiResponse<Object>> postCommunityCommentLike(
      @PathVariable("commentId") Long commentId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    LikeToggleResponse likeToggleResponse =
        communityService.postCommunityCommentLike(commentId, userDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(likeToggleResponse));
  }

  @GetMapping("/search/recent")
  @Operation(
      summary = "최근 검색어 조회",
      description =
          """
      - 사용자의 최근 검색어를 조회하는 API입니다. 최대 5개의 최근 검색어를 반환합니다.
      - 5개 이상의 검색어가 있는 경우, 가장 최근에 검색한 5개를 반환하며 이전 검색어는 삭제됩니다.
      - 검색어는 중복되지 않으며, 최대 30일 동안 저장됩니다.
      """)
  public ResponseEntity<ApiResponse<Object>> getRecentSearchKeywords(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.from(communityService.getRecentSearchKeywords(userDetails.getId())));
  }

  @DeleteMapping("/search/recent")
  @Operation(summary = "최근 검색어 삭제", description = "사용자의 최근 검색어 중에서 선택한 검색어를 삭제하는 API입니다.")
  public ResponseEntity<ApiResponse<Object>> deleteRecentSearchKeyword(
      @RequestParam("keyword") @Size(min = 2, max = 15) String keyword,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    communityService.deleteRecentSearchKeyword(userDetails.getId(), keyword);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @GetMapping("/search")
  @Operation(
      summary = "커뮤니티 게시글 검색",
      description =
          """
          - 커뮤니티 게시글을 검색하는 API입니다.
          - 검색어를 포함한 게시글을 조회하며, 검색어는 null일 수 없고 2자 이상 15자 이하이어야 합니다.
          - 로그인 후 검색시에 자동으로 최근 검색어에 추가됩니다.
          """)
  public ResponseEntity<ApiResponse<Object>> searchCommunityPost(
      @RequestParam("keyword") @Size(min = 2, max = 15) String keyword,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponse.from(
                communityService.searchCommunityPost(
                    keyword, page, size, userDetails == null ? null : userDetails.getId())));
  }

  @Operation(summary = "커뮤니티 게시글 수정", description = "커뮤니티 게시글을 수정하는 API입니다. 작성자만 수정할 수 있습니다.")
  @PatchMapping("/post/{postId}")
  public ResponseEntity<ApiResponse<Object>> updateCommunityPost(
      @PathVariable("postId") Long postId,
      @RequestBody @Valid CommunityPostRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    communityService.updateCommunityPost(postId, request, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }

  @DeleteMapping("/post/{postId}")
  @Operation(summary = "커뮤니티 게시글 삭제", description = "커뮤니티 게시글을 삭제하는 API입니다. 게시글 작성자만 삭제할 수 있습니다.")
  public ResponseEntity<ApiResponse<Object>> deleteCommunityPost(
      @PathVariable("postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    communityService.deleteCommunityPost(postId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }

  @DeleteMapping("/comment/{commentId}")
  @Operation(
      summary = "커뮤니티 게시글 댓글 삭제",
      description = "커뮤니티 게시글 댓글을 삭제하는 API입니다. 댓글 작성자만 삭제할 수 있습니다.")
  public ResponseEntity<ApiResponse<Object>> deleteCommunityComment(
      @PathVariable("commentId") Long commentId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    communityService.deleteComment(commentId, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.from(ApiResponse.EMPTY_RESPONSE));
  }

  @Operation(summary = "커뮤니티 댓글 수정", description = "커뮤니티 댓글을 수정하는 API입니다. 작성자만 수정할 수 있습니다.")
  @PatchMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse<Object>> updateCommunityComment(
      @PathVariable("commentId") Long commentId,
      @RequestBody @Valid CommunityCommentRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    communityService.updateComment(commentId, request, userDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.EMPTY_RESPONSE);
  }
}
