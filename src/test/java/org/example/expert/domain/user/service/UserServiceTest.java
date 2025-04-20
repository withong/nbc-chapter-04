package org.example.expert.domain.user.service;

import org.example.expert.common.config.PasswordEncoder;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.auth.exception.AuthErrorCode;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.exception.UserErrorCode;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void 사용자를_정상적으로_조회한다() {
        // given
        Long userId = 1L;

        User user = new User("test@test.com", "password", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponse result = userService.getUser(userId);

        // then
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void 사용자_조회_시_해당_사용자가_유효하지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.getUser(userId));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 비밀번호를_정상적으로_변경한다() {
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        UserChangePasswordRequest passwordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        User user = new User("test@test.com", "password", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn(newPassword);

        // when
        userService.changePassword(userId, passwordRequest);

        // then
        assertEquals(passwordRequest.getNewPassword(), user.getPassword());
    }

    @Test
    void 비밀번호_변경_시_사용자가_유효하지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        UserChangePasswordRequest passwordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, passwordRequest));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 기존_비밀번호와_동일한_비밀번호로_변경을_시도하면_예외가_발생한다() {
        // given
        Long userId = 1L;
        String oldPassword = "password";
        String newPassword = "password";
        UserChangePasswordRequest passwordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        User user = new User("test@test.com", "password", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, user.getPassword())).willReturn(true);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, passwordRequest));

        // then
        assertEquals(UserErrorCode.DUPLICATE_NEW_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    void 기존_비밀번호를_틀리면_예외가_발생한다() {
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        UserChangePasswordRequest passwordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        User user = new User("test@test.com", "password", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(newPassword, user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(false);

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, passwordRequest));

        // then
        assertEquals(AuthErrorCode.WRONG_PASSWORD.getMessage(), exception.getMessage());
    }
}