package com.dohui.user_service.domain.user;

import com.dohui.user_service.domain.user.dto.request.LoginRequest;
import com.dohui.user_service.domain.user.dto.response.LoginResponse;
import com.dohui.user_service.domain.user.dto.response.UserResponse;
import com.dohui.user_service.domain.user.dto.request.SignupRequest;
import com.dohui.user_service.global.exception.EmailAlreadyExistsException;
import com.dohui.user_service.global.exception.InvalidLoginException;
import com.dohui.user_service.global.exception.NicknameAlreadyExistsException;
import com.dohui.user_service.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserResponse signup(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException();
        }
        if(userRepository.existsByNickname(request.getNickname())){
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
                savedUser.getNickname()
        );
    }

    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(InvalidLoginException::new);
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidLoginException();
        }
        String token = jwtProvider.createToken(user.getId(), user.getEmail());
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        return new LoginResponse(token, userResponse);
    }

    public UserResponse getMyInfo(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("사용자 없음"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}
