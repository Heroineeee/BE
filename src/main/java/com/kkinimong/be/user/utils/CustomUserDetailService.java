package com.kkinimong.be.user.utils;

import static com.kkinimong.be.user.exception.errorcode.UserErrorCode.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.kkinimong.be.user.domain.User;
import com.kkinimong.be.user.exception.UserException;
import com.kkinimong.be.user.repository.UserRepository;

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
