package org.example.expert.domain.todo.service;

import org.example.expert.common.client.WeatherClient;
import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.exception.TodoErrorCode;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private WeatherClient weatherClient;
    @InjectMocks
    private TodoService todoService;

    @Test
    void 일정을_정상적으로_등록한다() {
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");

        User user = User.fromAuthUser(authUser);
        String weather = "weather";
        Todo newTodo = new Todo("title", "contents", weather, user);
        ReflectionTestUtils.setField(newTodo, "id", 123L);

        given(weatherClient.getTodayWeather()).willReturn(weather);
        given(todoRepository.save(any(Todo.class))).willReturn(newTodo);

        // when
        TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser, todoSaveRequest);

        // then
        assertNotNull(todoSaveResponse);

        assertEquals(newTodo.getId(), todoSaveResponse.getId());
        assertEquals(newTodo.getUser().getId(), todoSaveResponse.getUser().getId());
    }

    @Test
    void 일정_목록을_정상적으로_조회한다() {
        // given
        int page = 1;
        int size = 5;

        List<Todo> todoList = List.of(
                new Todo("title", "contents", "weather", new User()),
                new Todo("title", "contents", "weather", new User())
        );

        Page<Todo> todos = new PageImpl<>(todoList);

        given(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class))).willReturn(todos);

        // when
        Page<TodoResponse> result = todoService.getTodos(page, size);

        // then
        assertEquals(todos.getContent().size(), result.getContent().size());

        IntStream.range(0, result.getSize()).forEach(i -> {
            assertEquals(todos.getContent().get(i).getTitle(), result.getContent().get(i).getTitle());
            assertEquals(todos.getContent().get(i).getContents(), result.getContent().get(i).getContents());
        });
    }

    @Test
    void 일정_단건을_정상적으로_조회한다() {
        // given
        Long todoId = 1L;

        User user = new User("test@test.com", "password", UserRole.USER);
        Todo todo = new Todo("title", "contents", "weather", user);

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

        // when
        TodoResponse result = todoService.getTodo(todoId);

        // then
        assertNotNull(result);

        assertEquals(todo.getTitle(), result.getTitle());
        assertEquals(todo.getContents(), result.getContents());
        assertEquals(todo.getUser().getEmail(), result.getUser().getEmail());
    }

    @Test
    void 일정_단건_조회_시_일정이_존재하지_않으면_예외가_발생한다() {
        // given
        Long todoId = 1L;

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                todoService.getTodo(todoId));

        // then
        assertEquals(TodoErrorCode.TODO_NOT_FOUND.getMessage(), exception.getMessage());
    }
}