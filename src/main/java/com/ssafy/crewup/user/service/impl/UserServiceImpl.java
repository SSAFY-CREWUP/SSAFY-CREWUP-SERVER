package com.ssafy.crewup.user.service.impl;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.user.User;
import com.ssafy.crewup.user.dto.request.UserCreateRequest;
import com.ssafy.crewup.user.dto.response.UserGetResponse;
import com.ssafy.crewup.user.mapper.UserMapper;
import com.ssafy.crewup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j  // 로그 추가
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserGetResponse signup(UserCreateRequest request) {
        // 1. 이메일 중복 체크
        if (userMapper.countByEmail(request.getEmail()) > 0) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. User 엔티티 생성
        User user = request.toEntity(request.getPassword());

        // 3. DB 저장 (id 자동 설정됨)
        userMapper.insert(user);

        // 4. Response 반환 (조회 없이 바로)
        return UserGetResponse.from(user);
    }
}
