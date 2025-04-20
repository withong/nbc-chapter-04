package org.example.expert.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 요청 성공 시 토큰 반환")
    void signup_success() throws Exception {
        // given
        String email = "test@test.com";
        String password = "Password1@";
        String userRole = "USER";
        SignupRequest signupRequest = new SignupRequest(email, password, userRole);

        String bearerToken = "fake-token";
        SignupResponse signupResponse = new SignupResponse(bearerToken);

        given(authService.signup(any(SignupRequest.class))).willReturn(signupResponse);

        String json = objectMapper.writeValueAsString(signupRequest);

        // when
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value(bearerToken))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 요청 성공 시 토큰 반환")
    void signin_success() throws Exception {
        // given
        String email = "test@test.com";
        String password = "Password1@";
        SigninRequest signinRequest = new SigninRequest(email, password);

        String bearerToken = "fake-token";
        SigninResponse signinResponse = new SigninResponse(bearerToken);

        given(authService.signin(any(SigninRequest.class))).willReturn(signinResponse);

        String json = objectMapper.writeValueAsString(signinRequest);

        // when
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value(bearerToken))
                .andDo(print());
    }
}