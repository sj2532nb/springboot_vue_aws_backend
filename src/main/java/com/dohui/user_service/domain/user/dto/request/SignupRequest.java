package com.dohui.user_service.domain.user.dto.request;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
}
