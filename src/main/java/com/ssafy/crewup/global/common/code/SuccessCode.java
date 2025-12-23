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

    // 로그인/로그아웃 관련
    LOGIN_SUCCESS(HttpStatus.OK, "로그인이 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃이 완료되었습니다."),
	// 공통
	OK(HttpStatus.OK, "요청이 성공했습니다."),
	CREATED(HttpStatus.CREATED, "생성에 성공했습니다."),


    // Course
    COURSE_CREATE_SUCCESS(HttpStatus.CREATED, "코스 등록에 성공했습니다."),
    COURSE_READ_SUCCESS(HttpStatus.OK, "코스 상세 조회에 성공했습니다."),
    COURSE_LIST_SUCCESS(HttpStatus.OK, "코스 목록 조회에 성공했습니다."),
    COURSE_UPDATE_SUCCESS(HttpStatus.OK, "코스 수정에 성공했습니다."),
    COURSE_SCRAP_SUCCESS(HttpStatus.OK, "코스 스크랩 설정/해제에 성공했습니다."),
    COURSE_DELETE_SUCCESS(HttpStatus.OK, "코스 삭제에 성공했습니다."),

    // Course Review
    REVIEW_CREATE_SUCCESS(HttpStatus.CREATED, "리뷰 등록에 성공했습니다."),
    REVIEW_READ_SUCCESS(HttpStatus.OK, "리뷰 조회에 성공했습니다."),
    REVIEW_DELETE_SUCCESS(HttpStatus.OK, "리뷰 삭제에 성공했습니다.")
	;

	private final HttpStatus httpStatus;
	private final String message;

}

