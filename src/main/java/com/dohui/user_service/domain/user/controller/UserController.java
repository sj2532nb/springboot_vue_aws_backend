package com.dohui.user_service.domain.user.controller;

import com.dohui.user_service.domain.user.UserService;
import com.dohui.user_service.domain.user.dto.request.LoginRequest;
import com.dohui.user_service.domain.user.dto.request.SignupRequest;
import com.dohui.user_service.domain.user.dto.request.UpdateNicknameRequest;
import com.dohui.user_service.domain.user.dto.request.UpdatePasswordRequest;
import com.dohui.user_service.domain.user.dto.response.LoginResponse;
import com.dohui.user_service.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@RequestBody SignupRequest request){
        return userService.signup(request);
    }

    // 로그인
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return userService.login(request);
    }

    // 마이페이지
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return userService.getMyInfo(userId);
    }

    // 닉네임 변경
    @PatchMapping("/nickname")
    public void updateNickname(
            @RequestBody UpdateNicknameRequest request,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());
        userService.updateNickname(userId, request.getNickname());
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public void updatePassword(
            @RequestBody UpdatePasswordRequest request,
            Authentication authentication
    ){
        Long userId = Long.valueOf(authentication.getName());
        userService.updatePassword(userId, request);
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public void withdraw(Authentication authentication){
        Long userId = Long.valueOf(authentication.getName());
        userService.withdraw(userId);
    }


    // 테스트
    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @GetMapping("/auth-test")
    public String authTest(Authentication authentication) {
        return authentication.getName();
    }
}
