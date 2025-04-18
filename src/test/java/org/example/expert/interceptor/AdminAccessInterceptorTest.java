package org.example.expert.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(OutputCaptureExtension.class)
class AdminAccessInterceptorTest {

    @Test
    void preHandle_정상_호출_시_로그가_출력된다(CapturedOutput output) throws Exception {
        // given
        AdminAccessInterceptor adminInterceptor = new AdminAccessInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Object handler = new Object();

        given(request.getAttribute("userId")).willReturn(123L);
        given(request.getRequestURI()).willReturn("/admin/comments/456");

        // when
        boolean result = adminInterceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isTrue();

        assertThat(output).contains("Access ID  : 123")
                .contains("Access URI : /admin/comments/456")
                .contains("Access Time: ");
    }
}