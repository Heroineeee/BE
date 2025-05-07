package com.kkinikong.be.store.service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.domain.type.Role;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.exception.errorcode.UserErrorCode;
import com.kkinikong.be.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreCacheService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final UserRepository userRepository;

  private static final String KEY_PREFIX = "store-id:";

  public void saveStoreKakaoId(Long storeId, String kakaoId, Duration duration) {
    String key = KEY_PREFIX + storeId;
    redisTemplate.opsForValue().set(key, kakaoId, duration);
  }

  public Optional<String> getStoreKakaoId(Long storeId) {
    String key = KEY_PREFIX + storeId;
    Object value = redisTemplate.opsForValue().get(key);
    return Optional.ofNullable((String) value);
  }

  public String clearAllStoredKakaoId(Long userId) {
    User user =
        userRepository
            .findUserById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    // 관리자 권한을 가진 사용자는 캐시를 삭제할 수 없음
    if (user.getRole() == Role.ROLE_ADMIN) {
      throw new UserException(UserErrorCode.USER_NOT_AUTHORIZED);
    }

    Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
    for (String key : keys) {
      redisTemplate.delete(key);
    }
    return "카카오 ID 캐시 삭제 완료";
  }
}
