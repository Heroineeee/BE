package com.kkinikong.be.community.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kkinikong.be.community.domain.CommunityPost;
import com.kkinikong.be.community.repository.communityPost.CommunityPostRepository;
import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.repository.UserRepository;

@SpringBootTest
@DisplayName("CommunityService 좋아요 동시성 테스트")
class CommunityServiceTest {

  private static final Logger log = LoggerFactory.getLogger(CommunityServiceTest.class);
  @Autowired private CommunityService communityService;

  @Autowired private CommunityPostRepository communityPostRepository;

  @Autowired private UserRepository userRepository;

  private final int THREAD_COUNT = 100;
  private final List<Long> userIds = new ArrayList<>();

  @BeforeEach
  void setUp() {
    // 유저 100명 생성
    for (long i = 1; i <= THREAD_COUNT; i++) {
      User user =
          userRepository.save(
              User.basicLoginBuilder()
                  .email("user" + i + "@example.com")
                  .password("password" + i)
                  .buildBasicLogin());
      userIds.add(user.getId());
    }
  }

  @Test
  @DisplayName("서로 다른 유저들이 동시에 좋아요 누르는 경우")
  void testConcurrentLikeToggleWithDifferentUsers() throws InterruptedException {
    // given
    final long postId = 24; // 테스트용 게시글 ID
    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

    // when
    for (int i = 0; i < THREAD_COUNT; i++) {
      Long userId = userIds.get(i);
      executorService.execute(
          () -> {
            try {
              communityService.postCommunityPostLike(postId, userId);
            } finally {
              countDownLatch.countDown();
            }
          });
    }

    countDownLatch.await(); // 모든 스레드 작업 끝날 때까지 대기

    // then
    CommunityPost post = communityPostRepository.findById(postId).orElseThrow();
    System.out.println("최종 좋아요 수: " + post.getLikeCount());

    assertThat(post.getLikeCount()).isEqualTo(THREAD_COUNT); // 좋아요 100개가 되어야 함
  }
}
