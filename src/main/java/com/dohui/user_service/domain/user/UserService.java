package com.dohui.user_service.domain.user;

import com.dohui.user_service.domain.user.dto.request.LoginRequest;
import com.dohui.user_service.domain.user.dto.request.UpdatePasswordRequest;
import com.dohui.user_service.domain.user.dto.response.LoginResponse;
import com.dohui.user_service.domain.user.dto.response.UserResponse;
import com.dohui.user_service.domain.user.dto.request.SignupRequest;
import com.dohui.user_service.global.exception.EmailAlreadyExistsException;
import com.dohui.user_service.global.exception.InvalidLoginException;
import com.dohui.user_service.global.exception.InvalidPasswordException;
import com.dohui.user_service.global.exception.NicknameAlreadyExistsException;
import com.dohui.user_service.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    public UserResponse signup(SignupRequest request){
        if(userRepository.existsByEmailAndDeletedFalse(request.getEmail())){
            throw new EmailAlreadyExistsException();
        }
        if(userRepository.existsByNicknameAndDeletedFalse(request.getNickname())){
            throw new NicknameAlreadyExistsException();
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getNickname()
        );
        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getRole().name(),
                savedUser.getCreatedAt()
        );
    }

    // 로그인
    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail()).orElseThrow(InvalidLoginException::new);
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidLoginException();
        }
        String token = jwtProvider.createToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name(),
                user.getCreatedAt()
        );

        return new LoginResponse(token, userResponse);
    }

    // 마이페이지
    public UserResponse getMyInfo(Long userId){
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(()-> new RuntimeException("존재하지않는 유저입니다"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }

    // 닉네임 변경
    @Transactional
    public void updateNickname(Long userId, String newNickname){
        if(userRepository.existsByNickname(newNickname)){
            throw new NicknameAlreadyExistsException();
        }
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(()-> new RuntimeException("존재하지않는 유저입니다"));
        user.updateNickname(newNickname);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request){
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> new RuntimeException("존재하지않는 유저입니다"));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new InvalidPasswordException();
        }
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())){
            throw new RuntimeException("기존 비밀번호와 동일합니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encodedPassword);
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지않는 유저입니다"));
        if (user.isDeleted()){
            throw new IllegalStateException("탈퇴한 회원입니다");
        }
        user.softDelete();
    }

    // 유저 목록 조회
    public Page<UserResponse> getUsers(Pageable pageable){
        return userRepository.findByDeletedFalse(pageable)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getRole().name(),
                        user.getCreatedAt()
                ));
    }

    // 강제 탈퇴
    @Transactional
    public void forceDelete(Long adminId, Long targetUserId){
        if (adminId.equals(targetUserId)){
            throw new IllegalStateException("관리자 계정은 삭제할 수 없습니다");
        }

        User user = userRepository.findById(targetUserId).orElseThrow(()-> new IllegalArgumentException("존재하지않는 유저입니다"));

        if (user.isDeleted()){
            throw new IllegalStateException("탈퇴한 회원입니다");
        }
        user.softDelete();
    }
}
