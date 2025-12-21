package com.ssafy.crewup.user.controller;

import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.user.dto.request.UserCreateRequest;
import com.ssafy.crewup.user.dto.response.UserGetResponse;
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
    public ResponseEntity<ApiResponseBody<UserGetResponse>> signup(
            @Valid @RequestBody UserCreateRequest request,
            HttpSession session) {

        UserGetResponse response = userService.signup(request);

        // 회원가입 후 자동 로그인 (세션에 userId 저장)
        session.setAttribute("userId", response.getUserId());

        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.OK, response));
    }


}
