package com.ssafy.crewup.global.common.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {

	//TODO : 핵심 기능 or 도메인 별로 에러를 분류해주세요! ex. 검색, 필터링, 회원가입/로그인, 등등..

    // 회원가입 관련
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    USER_ADDITIONAL_INFO_SUCCESS(HttpStatus.OK,"추가정보 기입이 완료되었습니다."),
    USER_INFO_SUCCESS(HttpStatus.OK, "사용자 정보 조회 성공"),
    USER_UPDATE_SUCCESS(HttpStatus.OK, "사용자 정보 수정 성공"),
    PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "비밀번호 변경 성공"),

    // 로그인/로그아웃 관련
    LOGIN_SUCCESS(HttpStatus.OK, "로그인이 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃이 완료되었습니다."),
	// 공통
	OK(HttpStatus.OK, "요청이 성공했습니다."),
	CREATED(HttpStatus.CREATED, "생성에 성공했습니다.")

	;

	private final HttpStatus httpStatus;
	private final String message;

}

