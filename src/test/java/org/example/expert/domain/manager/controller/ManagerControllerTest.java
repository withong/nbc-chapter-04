package org.example.expert.domain.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.example.expert.common.config.JwtUtil;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ManagerService managerService;
    @MockBean
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("담당자 등록 요청 성공")
    void saveManager_success() throws Exception {
        // given
        long todoId = 2L;
        Long managerUserId = 3L;
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        UserResponse userResponse = new UserResponse(managerUserId, "manager@test.com");
        ManagerSaveResponse managerSaveResponse = new ManagerSaveResponse(authUser.getId(), userResponse);

        given(managerService.saveManager(any(AuthUser.class), anyLong(), any(ManagerSaveRequest.class)))
                .willReturn(managerSaveResponse);

        // when & then
        mockMvc.perform(post("/todos/" + todoId + "/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(managerSaveRequest))
                        .requestAttr("userId", authUser.getId())
                        .requestAttr("email", authUser.getEmail())
                        .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(managerSaveResponse.getId()))
                .andExpect(jsonPath("$.user.id").value(managerSaveResponse.getUser().getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("담당자 목록 조회 요청 성공")
    void getMembers_success() throws Exception {
        // given
        long todoId = 1L;

        UserResponse userResponse = new UserResponse(2L, "test@test.com");

        List<ManagerResponse> responseList = List.of(
                new ManagerResponse(3L, userResponse),
                new ManagerResponse(4L, userResponse),
                new ManagerResponse(5L, userResponse)
        );

        given(managerService.getManagers(anyLong())).willReturn(responseList);

        // when & then
        mockMvc.perform(get("/todos/" + todoId + "/managers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(responseList.size()))
                .andExpect(jsonPath("$[0].id").value(responseList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(responseList.get(1).getId()))
                .andExpect(jsonPath("$[2].id").value(responseList.get(2).getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("담당자 삭제 요청 성공")
    void deleteManager_success() throws Exception {
        // given
        long todoId = 1L;
        long userId = 3L;
        long managerId = 2L;
        String bearerToken = "Bearer fake-token";

        Claims claims = mock(Claims.class);

        given(jwtUtil.extractClaims(bearerToken.substring(7))).willReturn(claims);
        given(claims.getSubject()).willReturn(String.valueOf(userId));

        // when
        mockMvc.perform(delete("/todos/" + todoId + "/managers/" + managerId)
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        verify(managerService).deleteManager(userId, todoId, managerId);
    }
}