package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ManagerRepositoryTest {

    @Autowired
    ManagerRepository managerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TodoRepository todoRepository;

    @Test
    @DisplayName("담당자 목록 조회 성공")
    void findByTodoIdWithUser_success() {
        // given
        User user = userRepository.save(new User("test@test.com", "Password1@", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("title", "contents", "weather", user));

        managerRepository.save(new Manager(user, todo));
        managerRepository.save(new Manager(user, todo));

        // when
        List<Manager> found = managerRepository.findByTodoIdWithUser(todo.getId());

        // then
        assertThat(found).hasSize(3);

        assertThat(found)
                .extracting(manager -> manager.getUser().getEmail())
                .containsExactly("test@test.com", "test@test.com", "test@test.com");
    }
}