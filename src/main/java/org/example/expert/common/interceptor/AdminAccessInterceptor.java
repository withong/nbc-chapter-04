package org.example.expert.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 접근 ID
        Long userId = (Long) request.getAttribute("userId");
        // 접근 URI
        String accessUrl = request.getRequestURI();
        // 접근 시간
        LocalDateTime accessTime = LocalDateTime.now();

        // 접근 로그
        log.info("========== ADMIN URI ACCESS ==========");
        log.info("Access ID  : {}", userId);
        log.info("Access URI : {}", accessUrl);
        log.info("Access Time: {}", accessTime);
        log.info("======================================");

        return true;
    }
}
