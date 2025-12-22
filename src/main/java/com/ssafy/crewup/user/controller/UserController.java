package com.ssafy.crewup.user.controller;

import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.user.dto.request.LoginRequest;
import com.ssafy.crewup.user.dto.request.UserCreateRequest;
import com.ssafy.crewup.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    /**
     작성자 : 김성헌
     내용: 회원가입 ,로그인 API 구현
     */
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseBody<Void>> signup(
            @Valid @RequestBody UserCreateRequest request,
            HttpSession session) {

        Long userId = userService.signup(request);

        // 세션에 userId 저장 (자동 로그인)
        session.setAttribute("userId", userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SIGNUP_SUCCESS)
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseBody<Void>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        Long userId = userService.login(request);

        // 세션에 userId 저장
        session.setAttribute("userId", userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.LOGIN_SUCCESS)
        );

    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseBody<Void>> logout(HttpSession session) {

        // 세션 무효화
        session.invalidate();

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.LOGOUT_SUCCESS)
        );
    }
}


