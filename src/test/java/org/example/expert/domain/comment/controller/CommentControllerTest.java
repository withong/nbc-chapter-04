package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 등록 요청 성공")
    void saveComment_success() throws Exception {
        // given
        long todoId = 2L;
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        CommentSaveRequest commentSaveRequest = new CommentSaveRequest("contents");

        CommentSaveResponse commentSaveResponse = new CommentSaveResponse(
                3L, "contents", new UserResponse(authUser.getId(), authUser.getEmail()));

        given(commentService.saveComment(any(AuthUser.class), anyLong(), any(CommentSaveRequest.class)))
                .willReturn(commentSaveResponse);

        String json = objectMapper.writeValueAsString(commentSaveRequest);

        // when & then
        mockMvc.perform(post("/todos/" + todoId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .requestAttr("userId", authUser.getId())
                        .requestAttr("email", authUser.getEmail())
                        .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentSaveResponse.getId()))
                .andExpect(jsonPath("$.user.id").value(commentSaveResponse.getUser().getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getComments_success() throws Exception {
        // given
        long todoId = 1L;

        UserResponse userResponse = new UserResponse(4L, "test@test.com");

        List<CommentResponse> commentResponses = List.of(
                new CommentResponse(1L, "contents", userResponse),
                new CommentResponse(2L, "contents", userResponse),
                new CommentResponse(3L, "contents", userResponse)
        );

        given(commentService.getComments(anyLong())).willReturn(commentResponses);

        // when & then
        mockMvc.perform(get("/todos/" + todoId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(commentResponses.size()))
                .andExpect(jsonPath("$[0].id").value(commentResponses.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(commentResponses.get(1).getId()))
                .andExpect(jsonPath("$[2].id").value(commentResponses.get(2).getId()))
                .andDo(print());
    }
}