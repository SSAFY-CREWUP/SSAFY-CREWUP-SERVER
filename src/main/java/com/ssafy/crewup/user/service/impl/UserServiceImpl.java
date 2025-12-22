package com.ssafy.crewup.user.service.impl;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.user.User;
import com.ssafy.crewup.user.dto.request.LoginRequest;
import com.ssafy.crewup.user.dto.request.UserCreateRequest;
import com.ssafy.crewup.user.mapper.UserMapper;
import com.ssafy.crewup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long signup(UserCreateRequest request, String profileImageUrl) {
        // 1. 이메일 중복 체크
        int emailCount = userMapper.countByEmail(request.getEmail());

        if (emailCount > 0) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. User 엔티티 생성 (profileImageUrl 포함)
        User user = request.toEntity(request.getPassword(), profileImageUrl);

        // 3. DB 저장
        userMapper.insert(user);

        // 4. userId 반환
        return user.getId();
    }

    @Override
    public Long login(LoginRequest request) {
        // 1. 이메일로 사용자 조회
        User user = userMapper.findByEmail(request.getEmail());

        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 비밀번호 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. userId만 반환
        return user.getId();
    }
}

