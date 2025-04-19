package org.example.expert.domain.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSaveRequest {

    @NotBlank(message = "일정 제목을 입력하세요.")
    @Size(max = 200, message = "200자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "일정 내용을 입력하세요.")
    @Size(max = 1000, message = "1000자를 초과할 수 없습니다.")
    private String contents;
}
