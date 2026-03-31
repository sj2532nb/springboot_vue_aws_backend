package com.dohui.user_service.domain.user.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String role;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String email, String nickname, String role, LocalDateTime createdAt){
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.createdAt = createdAt;
    }
}
