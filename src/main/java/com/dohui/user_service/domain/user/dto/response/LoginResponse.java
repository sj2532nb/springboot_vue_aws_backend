package com.dohui.user_service.domain.user.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final UserResponse user;

    public LoginResponse(String accessToken, UserResponse user){
        this.accessToken = accessToken;
        this.user = user;
    }
}
