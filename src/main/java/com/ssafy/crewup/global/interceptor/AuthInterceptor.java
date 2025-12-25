package com.ssafy.crewup.global.interceptor;

import com.ssafy.crewup.global.config.SessionStore;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionStore sessionStore;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 요청은 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String sessionId = request.getHeader("X-Session-Id");

        log.info("🔍 AuthInterceptor - URI: {}, SessionId: {}", request.getRequestURI(), sessionId);

        if (sessionId == null || !sessionStore.containsKey(sessionId)) {
            log.error("❌ 인증 실패 - sessionId: {}", sessionId);
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = sessionStore.get(sessionId);
        log.info("✅ 인증 성공 - userId: {}", userId);
        request.setAttribute("userId", userId);

        return true;
    }
}