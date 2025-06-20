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
import com.kkinikong.be.global.exception.handler.CustomAccessDeniedHandler;
import com.kkinikong.be.global.exception.handler.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    // Swagger 관련 전체 허용
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/swagger-config")
                    .permitAll()
                    // 관리자만 허용
                    .requestMatchers("/api/v1/store/upload/**")
                    .hasAuthority("ROLE_ADMIN")
                    // 인증 필요
                    .requestMatchers(
                        "/api/v1/user/**",
                        "/api/v1/store/scrap/**",
                        "/api/v1/report/**",
                        "/api/v1/cache/**",
                        "/api/v1/batch/**")
                    .authenticated()
                    // 리뷰,편의점,커뮤니티 관련 POST 인증 필요
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/*/review",
                        "/api/v1/*/review/*/photo",
                        "/api/v1/convenience/post",
                        "/api/v1/convenience/post/**",
                        "/api/v1/community/post/**",
                        "/api/v1/community/comment/**")
                    .authenticated()
                    // 리뷰,편의점 관련 DELETE 인증 필요
                    .requestMatchers(
                        HttpMethod.DELETE, "/api/v1/*/review/*", "/api/v1/convenience/post/**")
                    .authenticated()
                    // 편의점 추천 GET 요청 인증 필요
                    .requestMatchers(HttpMethod.GET, "/api/v1/convenience/recommendation")
                    .authenticated()
                    // 그 외 모든 요청 허용
                    .anyRequest()
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
            "http://localhost:5173",
            "https://kkinikong.store",
            "https://kkinicong.vercel.app")); // 추후 배포 시 변경 필요
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(
        Arrays.asList(
            "X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token", "Accept"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
