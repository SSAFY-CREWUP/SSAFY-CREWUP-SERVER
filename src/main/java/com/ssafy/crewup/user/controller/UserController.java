package com.ssafy.crewup.user.controller;

import com.ssafy.crewup.global.annotation.LoginUser;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.global.config.SessionStore;
import com.ssafy.crewup.global.service.S3Service;
import com.ssafy.crewup.user.dto.request.*;
import com.ssafy.crewup.user.dto.response.UserResponse;
import com.ssafy.crewup.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private final SessionStore sessionStore;

    //회원가입
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody<Map<String, String>>> signup(
            @Valid @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        // 1. 프로필 이미지 S3 업로드 (파일이 있을 때만)
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(profileImage, "profiles");
        }

        // 2. 회원가입
        Long userId = userService.signup(request, profileImageUrl);

        // 3. 세션 ID 생성 및 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("sessionId", sessionId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SIGNUP_SUCCESS, response)
        );
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponseBody<Map<String, String>>> login(
            @Valid @RequestBody LoginRequest request) {

        Long userId = userService.login(request);

        // 세션 ID 생성 및 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("sessionId", sessionId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.LOGIN_SUCCESS, response)
        );
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseBody<Void>> logout(HttpServletRequest request) {

        String sessionId = request.getHeader("X-Session-Id");

        if (sessionId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 세션 삭제
        sessionStore.remove(sessionId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.LOGOUT_SUCCESS)
        );
    }

    // 추가 정보 등록
    @PutMapping("/add/info")
    public ResponseEntity<ApiResponseBody<Void>> updateAdditionalInfo(
            @Valid @RequestBody UserAdditionalInfoRequest request,
            @LoginUser Long userId) {

        userService.updateAdditionalInfo(userId, request);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.USER_ADDITIONAL_INFO_SUCCESS)
        );
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponseBody<UserResponse>> getMyPage(@LoginUser Long userId) {

        UserResponse userResponse = userService.getUserInfo(userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.USER_INFO_SUCCESS, userResponse)
        );
    }

    // 마이페이지 전체 정보 수정
    @PutMapping(value = "/edit/mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody<Void>> updateMyPage(
            @Valid @RequestPart("request") UserUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @LoginUser Long userId) {

        // 프로필 이미지 업로드 (있는 경우)
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(profileImage, "profiles");
        }

        // 전체 정보 업데이트
        userService.updateUserInfo(userId, request, profileImageUrl);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.USER_UPDATE_SUCCESS)
        );
    }

    // 비밀번호 변경
    @PutMapping("/edit/password")
    public ResponseEntity<ApiResponseBody<Void>> updatePassword(
            @Valid @RequestBody PasswordUpdateRequest request,
            @LoginUser Long userId) {

        userService.updatePassword(userId, request);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.PASSWORD_UPDATE_SUCCESS)
        );
    }
}
