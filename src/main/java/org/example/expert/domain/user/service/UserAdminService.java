package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.exception.UserErrorCode;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    @Transactional
    public void changeUserRole(long userId, UserRoleChangeRequest userRoleChangeRequest) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new InvalidRequestException(UserErrorCode.USER_NOT_FOUND));
        user.updateRole(UserRole.of(userRoleChangeRequest.getRole()));
    }
}
