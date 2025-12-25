package com.ssafy.crewup.global.interceptor;

import com.ssafy.crewup.global.config.SessionStore;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionStore sessionStore;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String sessionId = request.getHeader("X-Session-Id");

        if (sessionId == null || !sessionStore.containsKey(sessionId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = sessionStore.get(sessionId);
        request.setAttribute("userId", userId);

        return true;
    }
}