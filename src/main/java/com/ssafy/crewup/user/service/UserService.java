package com.ssafy.crewup.user.service;

import com.ssafy.crewup.user.dto.request.UserCreateRequest;
import com.ssafy.crewup.user.dto.response.UserGetResponse;


public interface UserService {
    /**
     회원가입
     */
    UserGetResponse signup(UserCreateRequest request);

}
