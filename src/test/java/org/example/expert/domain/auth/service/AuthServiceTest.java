package org.example.expert.domain.auth.service;

import org.example.expert.common.config.JwtUtil;
import org.example.expert.common.config.PasswordEncoder;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthErrorCode;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.exception.UserErrorCode;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입_성공() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@test.com", "Password1@", "USER");

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(signupRequest.getPassword());

        User newUser = new User(signupRequest.getEmail(), signupRequest.getPassword(), UserRole.USER);
        ReflectionTestUtils.setField(newUser, "id", 1L);

        given(userRepository.save(any(User.class))).willReturn(newUser);
        given(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).willReturn("fake-token");

        // when
        SignupResponse result = authService.signup(signupRequest);

        // then
        assertEquals("fake-token", result.getBearerToken());
    }

    @Test
    void 이미_가입된_이메일로_회원가입을_시도하면_예외가_발생한다() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@test.com", "Password1@", "USER");

        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                authService.signup(signupRequest));

        // then
        assertEquals(UserErrorCode.DUPLICATE_EMAIL.getMessage(), exception.getMessage());

    }

    @Test
    void 로그인_성공() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "Password1@");

        User user = new User(signinRequest.getEmail(), signinRequest.getPassword(), UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).willReturn("fake-token");

        // when
        SigninResponse result = authService.signin(signinRequest);

        // then
        assertEquals("fake-token", result.getBearerToken());
    }

    @Test
    void 가입되지_않은_이메일로_로그인을_시도하면_예외가_발생한다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "Password1@");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                authService.signin(signinRequest));

        // then
        assertEquals(UserErrorCode.NOT_REGISTERED_USER.getMessage(), exception.getMessage());
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않으면_예외가_발생한다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "Password1@");
        User user = new User(signinRequest.getEmail(), signinRequest.getPassword(), UserRole.USER);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when
        AuthException exception = assertThrows(AuthException.class, () ->
                authService.signin(signinRequest));

        // then
        assertEquals(AuthErrorCode.WRONG_PASSWORD.getMessage(), exception.getMessage());
    }
}