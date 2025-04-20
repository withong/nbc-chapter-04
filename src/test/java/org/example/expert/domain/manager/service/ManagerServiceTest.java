package org.example.expert.domain.manager.service;

import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.exception.ManagerErrorCode;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.exception.TodoErrorCode;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.exception.UserErrorCode;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Test
    public void manager_목록_조회_시_Todo가_없다면_InvalidRequestException_에러를_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.getManagers(todoId));
        assertEquals(TodoErrorCode.TODO_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        long todoId = 1L;
        long managerUserId = 2L;

        Todo todo = new Todo();
        ReflectionTestUtils.setField(todo, "user", null);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest)
        );

        assertEquals(ManagerErrorCode.INVALID_MANAGER_REQUESTER.getMessage(), exception.getMessage());
    }

    @Test
    void 로그인된_사용자와_일정_작성자가_일치하지_않으면_예외를_던진다() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);

        User todoUser = new User("todo@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(todoUser, "id", 2L);

        Long todoId = 1L;

        Long managerUserId = 99L;
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        Todo todo = new Todo("title", "contents", "weather", todoUser);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest));

        // then
        assertEquals(ManagerErrorCode.INVALID_MANAGER_REQUESTER.getMessage(), exception.getMessage());
    }

    @Test
    void manager로_등록할_사용자가_유효하지_않으면_예외를_던진다() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Long todoId = 1L;

        Long managerUserId = 99L;
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        Todo todo = new Todo("title", "contents", "weather", user);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest));

        // then
        assertEquals(ManagerErrorCode.MANAGER_NOT_FOUND.getMessage(), exception.getMessage());

    }

    @Test
    void 일정_작성자를_일정_담당자로_등록하려고_하면_예외를_던진다() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        Long todoId = 1L;

        Long managerUserId = 2L;
        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

        Todo todo = new Todo("title", "contents", "weather", user);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.saveManager(authUser, todoId, managerSaveRequest));

        // then
        assertEquals(ManagerErrorCode.INVALID_MANAGER_SELF_ASSIGN.getMessage(), exception.getMessage());
    }

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        Manager mockManager = new Manager(todo.getUser(), todo);
        List<Manager> managerList = List.of(mockManager);

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

        // when
        List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

        // then
        assertEquals(1, managerResponses.size());
        assertEquals(mockManager.getId(), managerResponses.get(0).getId());
        assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }

    @Test
        // 테스트코드 샘플
    void 일정의_담당자를_정상적으로_등록한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

        long todoId = 1L;
        Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

        long managerUserId = 2L;
        User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
        ReflectionTestUtils.setField(managerUser, "id", managerUserId);

        ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId); // request dto 생성

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
        given(managerRepository.save(any(Manager.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(managerUser.getId(), response.getUser().getId());
        assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    void 일정의_담당자를_정상적으로_삭제한다() {
        Long userId = 1L;
        Long todoId = 2L;
        Long managerId = 3L;

        User user = new User("test@test.com", "password", UserRole.USER);
        Todo todo = new Todo("title", "contents", "weather", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        Manager manager = new Manager(user, todo);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

        // when
        managerService.deleteManager(userId, todoId, managerId);

        // then
        Mockito.verify(managerRepository).delete(manager);
    }

    @Test
    void 사용자가_유효하지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long todoId = 2L;
        Long managerId = 3L;

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(userId, todoId, managerId));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 일정이_유효하지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long todoId = 2L;
        Long managerId = 3L;

        User user = new User("test@test.com", "password", UserRole.USER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(userId, todoId, managerId));

        // then
        assertEquals(TodoErrorCode.TODO_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 일정의_작성자가_null_이면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long todoId = 2L;
        Long managerId = 3L;

        User user = new User("test@test.com", "password", UserRole.USER);
        Todo todo = new Todo("title", "contents", "weather", null);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(userId, todoId, managerId));

        // then
        assertEquals(TodoErrorCode.INVALID_TODO_OWNER.getMessage(), exception.getMessage());
    }

    @Test
    void 로그인된_작성자와_일정의_작성자가_일치하지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long todoId = 2L;
        Long managerId = 3L;

        User user = new User("test@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        User todoUser = new User("todo@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 99L);

        Todo todo = new Todo("title", "contents", "weather", todoUser);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(userId, todoId, managerId));

        // then
        assertEquals(TodoErrorCode.INVALID_TODO_OWNER.getMessage(), exception.getMessage());
    }

    @Test
    void 일정의_담당자가_유효하지_않으면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long todoId = 2L;
        Long managerId = 3L;

        User user = new User("test@test.com", "password", UserRole.USER);
        Todo todo = new Todo("title", "contents", "weather", user);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(userId, todoId, managerId));

        // then
        assertEquals(ManagerErrorCode.MANAGER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 일정에_등록된_담당자가_아니면_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long todoId = 2L;
        Long managerId = 3L;

        User user = new User("test@test.com", "password", UserRole.USER);
        Todo todo = new Todo("title", "contents", "weather", user);
        ReflectionTestUtils.setField(todo, "id", 2L);

        Todo other = new Todo();
        ReflectionTestUtils.setField(other, "id", 99L);
        Manager manager = new Manager(user, other);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                managerService.deleteManager(userId, todoId, managerId));

        // then
        assertEquals(ManagerErrorCode.NOT_MANAGER_OF_TODO.getMessage(), exception.getMessage());
    }
}
