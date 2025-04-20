package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TodoService todoService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("일정 등록 요청 성공")
    void saveTodo_success() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");

        UserResponse userResponse = new UserResponse(authUser.getId(), authUser.getEmail());

        TodoSaveResponse todoSaveResponse =
                new TodoSaveResponse(2L, "title", "contents", "weather", userResponse);

        given(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class)))
                .willReturn(todoSaveResponse);

        // when & then
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(todoSaveRequest))
                        .requestAttr("userId", authUser.getId())
                        .requestAttr("email", authUser.getEmail())
                        .requestAttr("userRole", authUser.getUserRole().name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoSaveResponse.getId()))
                .andExpect(jsonPath("$.title").value(todoSaveResponse.getTitle()))
                .andExpect(jsonPath("$.contents").value(todoSaveResponse.getContents()))
                .andExpect(jsonPath("$.user.id").value(todoSaveResponse.getUser().getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("일정 목록 조회 요청 성공")
    void getTodos_success() throws Exception {
        // given
        int page = 1;
        int size = 5;

        UserResponse userResponse = new UserResponse(9L, "test@test.com");
        LocalDateTime time = LocalDateTime.now();

        List<TodoResponse> todos = List.of(
                new TodoResponse(1L, "title", "contents", "weather", userResponse, time, time),
                new TodoResponse(2L, "title", "contents", "weather", userResponse, time, time),
                new TodoResponse(3L, "title", "contents", "weather", userResponse, time, time)
        );

        Page<TodoResponse> todoResponses = new PageImpl<>(
                todos, PageRequest.of(page - 1, size), todos.size()
        );

        given(todoService.getTodos(anyInt(), anyInt())).willReturn(todoResponses);

        // when & then
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(todoResponses.getContent().size()))
                .andExpect(jsonPath("$.content[0].id").value(todoResponses.getContent().get(0).getId()))
                .andExpect(jsonPath("$.content[1].id").value(todoResponses.getContent().get(1).getId()))
                .andExpect(jsonPath("$.content[2].id").value(todoResponses.getContent().get(2).getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("일정 단건 조회 요청 성공")
    void getTodo_success() throws Exception {
        // given
        long todoId = 1L;

        UserResponse userResponse = new UserResponse(9L, "test@test.com");
        LocalDateTime time = LocalDateTime.now();

        TodoResponse todoResponse =
                new TodoResponse(1L, "title", "contents", "weather", userResponse, time, time);

        given(todoService.getTodo(anyLong())).willReturn(todoResponse);

        // when & then
        mockMvc.perform(get("/todos/" + todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoResponse.getId()))
                .andExpect(jsonPath("$.user.id").value(todoResponse.getUser().getId()))
                .andDo(print());
    }
}