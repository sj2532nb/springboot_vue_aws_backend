package com.dohui.user_service.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.dohui.user_service.global.jwt.JwtAuthenticationFilter;
import com.dohui.user_service.global.jwt.JwtProvider;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtProvider jwtProvider) {
//        return new JwtAuthenticationFilter(jwtProvider);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())  // postman 테스트용
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/users/signup",
                                "/api/users/login",
                                "/api/upload/**",
                                "/images/**",
                                "/files/**",
                                "/api/files/**",
                                "/api/s3/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()  // 인증없이 허용
                        .requestMatchers(HttpMethod.GET, "/api/posts/*/previous").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()  // 글 목록은 로그인 없이 허용
                        .requestMatchers("/api/users/auth-test", "/api/users/**", "/api/posts/**", "/api/comments/**").hasAnyRole("USER", "ADMIN")  // 나머지 users, posts, comments는 로그인 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}