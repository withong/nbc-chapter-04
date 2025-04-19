package org.example.expert.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Component
@Aspect
public class AdminApiLoggingAop {

    private final ObjectMapper objectMapper;

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 요청 시각
        LocalDateTime requestTime = LocalDateTime.now();

        // 2. 요청된 메서드 정보
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        // 3. 요청 본문
        Object[] args = joinPoint.getArgs();
        String requestBody = objectMapper.writeValueAsString(args);

        // 4. 요청 로그
        log.info("========== ADMIN API REQUEST ==========");
        log.info("Request Time  : {}", requestTime);
        log.info("Request Class : {}", className);
        log.info("Request Method: {}", methodName);
        log.info("Request Body  : {}", requestBody);

        // 5. 메서드 실행
        Object response = joinPoint.proceed();

        // 6. 응답 본문
        String responseBody = objectMapper.writeValueAsString(response);

        // 7. 응답 로그
        log.info("Response Body : {}", responseBody);
        log.info("=======================================");

        return response;
    }
}