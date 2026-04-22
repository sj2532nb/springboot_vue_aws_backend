package com.dohui.user_service.global.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // swagger 관련 경로는 JWT 검사 없이 통과
        String uri = request.getRequestURI();

        if (
                uri.startsWith("/api/users/login") ||
                uri.startsWith("/api/users/signup") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/swagger-ui")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 가져오기
        String authHeader = request.getHeader("Authorization");

        // Bearer 토큰 아니면 그냥 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 제거 → 순수 토큰
        String token = authHeader.substring(7);

        try {
            // 토큰 파싱 (검증 포함)
            Claims claims = jwtProvider.parseClaims(token);

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            // 토큰 이상하면 그냥 인증 없이 통과
            SecurityContextHolder.clearContext();
        }
        // 다음 필터로
        filterChain.doFilter(request, response);
    }
}