package com.dohui.user_service.domain.user;

import com.dohui.user_service.domain.user.dto.request.LoginRequest;
import com.dohui.user_service.domain.user.dto.request.SignupRequest;
import com.dohui.user_service.domain.user.dto.response.LoginResponse;
import com.dohui.user_service.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@RequestBody SignupRequest request){
        return userService.signup(request);
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return userService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return userService.getMyInfo(userId);
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @GetMapping("/auth-test")
    public String authTest(Authentication authentication) {
        return authentication.getName();
    }
}
