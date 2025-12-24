package com.ssafy.crewup.user.service;

import com.ssafy.crewup.user.dto.request.*;
import com.ssafy.crewup.user.dto.response.UserResponse;


public interface UserService {
    //회원가입
    Long signup(UserCreateRequest request,String profileImageUrl);
    //로그인
    Long login(LoginRequest request);
    //유저 추가정보
    void updateAdditionalInfo(Long userId, UserAdditionalInfoRequest request);
    // 마이페이지 조회
    UserResponse getUserInfo(Long userId);

    // 마이페이지 전체 정보 수정
    void updateUserInfo(Long userId, UserUpdateRequest request, String profileImageUrl);

    // 비밀번호 변경
    void updatePassword(Long userId, PasswordUpdateRequest request);
}
