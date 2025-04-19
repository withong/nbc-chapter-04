package org.example.expert.domain.comment.service;

import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.exception.CommentErrorCode;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    void 댓글_삭제_성공() {
        // given
        User user = new User("test@test.com", "password", UserRole.USER);
        Todo todo = new Todo("title", "contents", "weather", user);

        Long commentId = 1L;
        Comment comment = new Comment("contents", user, todo);

        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

         // when
        commentAdminService.deleteComment(commentId);

        // then
        Mockito.verify(commentRepository).deleteById(commentId);
    }

    @Test
    void 삭제할_댓글이_존재하지_않으면_예외가_발생한다() {
        // given
        Long commentId = 1L;
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                commentAdminService.deleteComment(commentId));

        // then
        assertEquals(CommentErrorCode.COMMENT_NOT_FOUND.getMessage(), exception.getMessage());
    }
}