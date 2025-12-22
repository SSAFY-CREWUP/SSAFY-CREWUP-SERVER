package com.ssafy.crewup.user.controller;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.global.service.S3Service;
import com.ssafy.crewup.user.dto.request.LoginRequest;
import com.ssafy.crewup.user.dto.request.UserAdditionalInfoRequest;
import com.ssafy.crewup.user.dto.request.UserCreateRequest;
import com.ssafy.crewup.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    /**
     작성자 : 김성헌
     내용: 회원가입 ,로그인,로그아웃 API 구현
     */
    private final UserService userService;
    private final S3Service s3Service;

    //회원가입
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody<Void>> signup(
            @Valid @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpSession session) {

        // 1. 프로필 이미지 S3 업로드
        String profileImageUrl = s3Service.uploadFile(profileImage, "profiles");

        // 2. 회원가입
        Long userId = userService.signup(request, profileImageUrl);

        // 3. 세션 저장
        session.setAttribute("userId", userId);

        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.SIGNUP_SUCCESS));
    }
    //로그인
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
    //로그아웃

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseBody<Void>> logout(HttpSession session) {

        // 세션에 userId가 없으면 (이미 로그아웃 상태)
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 세션 무효화
        session.invalidate();

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.LOGOUT_SUCCESS)
        );
    }
    // 추가 정보 등록
    @PutMapping("/add/info")
    public ResponseEntity<ApiResponseBody<Void>> updateAdditionalInfo(
            @Valid @RequestBody UserAdditionalInfoRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        userService.updateAdditionalInfo(userId, request);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.USER_ADDITIONAL_INFO_SUCCESS)
        );
    }
}

