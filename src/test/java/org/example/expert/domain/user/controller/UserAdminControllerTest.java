package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원_권한을_정상적으로_변경하고_AOP_로그가_출력ㄴ된다(CapturedOutput output) throws Exception {
        // given
        User user = userRepository.save(new User("user@test.com", "Password1@", UserRole.USER));
        Long userId = user.getId();

        User admin = userRepository.save(new User("admin@test.com", "Password1@", UserRole.ADMIN));
        Long adminId = admin.getId();

        UserRoleChangeRequest UserRoleChangeRequest = new UserRoleChangeRequest("ADMIN");
        String json = objectMapper.writeValueAsString(UserRoleChangeRequest);

        // when
        mockMvc.perform(patch("/admin/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .requestAttr("userId", adminId))
                .andExpect(status().isOk());

        // then: AOP 로그 검증
        assertThat(output)
                .contains("========== ADMIN API REQUEST ==========")
                .contains("Request Time  : ")
                .contains("Request Class : ")
                .contains("Request Method: ")
                .contains("Request Body  : ")
                .contains("Response Body : ")
                .contains("=======================================");
    }
}