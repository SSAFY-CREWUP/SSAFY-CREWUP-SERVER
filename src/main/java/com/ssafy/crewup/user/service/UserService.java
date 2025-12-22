package com.ssafy.crewup.user.service;

import com.ssafy.crewup.user.dto.request.LoginRequest;
import com.ssafy.crewup.user.dto.request.UserCreateRequest;


public interface UserService {
    /**
     회원가입
     */
    Long signup(UserCreateRequest request,String profileImageUrl);
    Long login(LoginRequest request);
}

