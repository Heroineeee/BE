package com.kkinikong.be.community.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OptimisticLockLikeFacade {

  private final CommunityService communityService;

  public void tryPostLike(Long postId, Long userId) {
    while (true) {
      try {
        communityService.postCommunityPostLike(postId, userId);
        break;
      } catch (Exception e) {
        try {
          Thread.sleep(100);
        } catch (StaleObjectStateException | InterruptedException ex) {
          System.out.println("ERROR!!!");
          throw new RuntimeException(ex);
        }
      }
    }
  }
}
