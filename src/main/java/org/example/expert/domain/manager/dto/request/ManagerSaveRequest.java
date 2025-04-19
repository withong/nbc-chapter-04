package org.example.expert.domain.manager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerSaveRequest {

    @NotNull(message = "담당자로 등록할 사용자 ID를 입력하세요.")
    private Long managerUserId; // 일정 작상자가 배치하는 유저 id
}
