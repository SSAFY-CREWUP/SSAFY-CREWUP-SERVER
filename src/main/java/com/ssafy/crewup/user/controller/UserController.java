package com.ssafy.crewup.user.controller;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.global.service.S3Service;
import com.ssafy.crewup.user.dto.request.*;
import com.ssafy.crewup.user.dto.response.UserResponse;
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

        // 1. 프로필 이미지 S3 업로드 (파일이 있을 때만)
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadFile(profileImage, "profiles");
        }

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

    // 마이페이지 조회
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponseBody<UserResponse>> getMyPage(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

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
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

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
    @PutMapping("/password")
    public ResponseEntity<ApiResponseBody<Void>> updatePassword(
            @Valid @RequestBody PasswordUpdateRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        userService.updatePassword(userId, request);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.PASSWORD_UPDATE_SUCCESS)
        );
    }
}
