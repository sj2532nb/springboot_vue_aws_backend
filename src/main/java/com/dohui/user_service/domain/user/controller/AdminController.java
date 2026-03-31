package com.dohui.user_service.domain.user.controller;

import com.dohui.user_service.domain.user.UserService;
import com.dohui.user_service.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    // 유저 목록 조회
    @GetMapping("/users")
    public Page<UserResponse> getUsers(Pageable pageable){
        return userService.getUsers(pageable);
    }

    // 강제 탈퇴
    @DeleteMapping("/users/{id}")
    public void deleteUser(
            @PathVariable Long id,
            Authentication authentication
    ){
        Long adminId = Long.valueOf(authentication.getName());
        userService.forceDelete(adminId, id);
    }
}
