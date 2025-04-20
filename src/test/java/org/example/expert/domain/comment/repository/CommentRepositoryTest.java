package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TodoRepository todoRepository;

    @Test
    @DisplayName("일정의 댓글 목록 조회 성공")
    void findByTodoIdWithUser_success() {
        // given
        User user = userRepository.save(new User("test@test.com", "Password1@", UserRole.USER));
        Todo todo = todoRepository.save(new Todo("title", "contents", "weather", user));

        commentRepository.save(new Comment("contents1", user, todo));
        commentRepository.save(new Comment("contents2", user, todo));

        // when
        List<Comment> found = commentRepository.findByTodoIdWithUser(todo.getId());

        // then
        assertThat(found).hasSize(2);

        assertThat(found)
                .extracting("contents")
                .containsExactly("contents1", "contents2");
    }
}