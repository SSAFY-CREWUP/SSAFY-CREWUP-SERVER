package com.ssafy.crewup.user.service;

import com.ssafy.crewup.user.dto.request.LoginRequest;
import com.ssafy.crewup.user.dto.request.UserAdditionalInfoRequest;
import com.ssafy.crewup.user.dto.request.UserCreateRequest;


public interface UserService {
    //회원가입
    Long signup(UserCreateRequest request,String profileImageUrl);
    //로그인
    Long login(LoginRequest request);
    //유저 추가정보
    void updateAdditionalInfo(Long userId, UserAdditionalInfoRequest request);
}

