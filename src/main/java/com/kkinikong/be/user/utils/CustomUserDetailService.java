package com.kkinikong.be.user.utils;

import static com.kkinikong.be.user.exception.errorcode.UserErrorCode.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.user.domain.User;
import com.kkinikong.be.user.exception.UserException;
import com.kkinikong.be.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    User user =
        userRepository
            .findUserById(Long.parseLong(id))
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

    return new CustomUserDetails(user.getId(), user.getNickname());
  }
}
