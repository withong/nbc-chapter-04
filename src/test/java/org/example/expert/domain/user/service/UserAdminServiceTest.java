package org.example.expert.domain.user.service;

import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void 사용자의_권한을_정상적으로_변경한다() {
        // given
        Long userId = 1L;
        String role = "ADMIN";
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest(role);

        User user = new User("test@test.com", "password", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        userAdminService.changeUserRole(userId, userRoleChangeRequest);

        // then
        assertEquals(UserRole.of(userRoleChangeRequest.getRole()), user.getUserRole());
    }

    @Test
    void 사용자가_유효하지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        String role = "ADMIN";
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest(role);

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userAdminService.changeUserRole(userId, userRoleChangeRequest));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }
}