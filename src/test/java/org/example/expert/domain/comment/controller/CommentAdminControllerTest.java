package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
class CommentAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentAdminService commentAdminService;

    @Test
    @DisplayName("댓글 삭제 요청 성공")
    void deleteComment_success() throws Exception {
        // given
        Long commentId = 1L;

        // when
        mockMvc.perform(delete("/admin/comments/" + commentId))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        Mockito.verify(commentAdminService).deleteComment(commentId);
    }
}