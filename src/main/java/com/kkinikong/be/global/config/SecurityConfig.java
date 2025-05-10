package com.kkinikong.be.global.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

import com.kkinikong.be.auth.filter.JwtAuthFilter;
import com.kkinikong.be.auth.util.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/swagger-config")
                    .permitAll()
                    .requestMatchers(
                        "/api/v1/user/**",
                        "/api/v1/store/scrap/**",
                        "/api/v1/report/**",
                        "/api/v1/cache/**",
                        "/api/v1/batch/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/*/review")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/*/review/*/photo")
                    .authenticated()
                    .anyRequest() // 그 외 모든 요청 허용
                    .permitAll())
        .addFilterBefore(
            new JwtAuthFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(
        Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:5173")); // 추후 배포 시 변경 필요
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(
        Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
