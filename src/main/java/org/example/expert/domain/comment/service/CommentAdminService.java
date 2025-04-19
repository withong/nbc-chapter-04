package org.example.expert.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.exception.CommentErrorCode;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.common.exception.InvalidRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentAdminService {

    private final CommentRepository commentRepository;

    @Transactional
    public void deleteComment(long commentId) {
        commentRepository.findById(commentId)
                        .orElseThrow(() -> new InvalidRequestException(CommentErrorCode.COMMENT_NOT_FOUND));

        commentRepository.deleteById(commentId);
    }
}
