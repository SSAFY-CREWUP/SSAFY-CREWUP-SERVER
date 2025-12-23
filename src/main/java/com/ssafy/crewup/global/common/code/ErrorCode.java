package com.ssafy.crewup.global.common.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

	//TODO : 핵심 기능 or 도메인 별로 에러를 분류해주세요! ex. 검색, 필터링, 회원가입/로그인, 등등..

    // 회원 관련
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // 크루 생성 관련
    INVALID_REGION(HttpStatus.BAD_REQUEST, "유효하지 않은 지역입니다."),
    INVALID_ACTIVITY_TIME(HttpStatus.BAD_REQUEST, "유효하지 않은 활동 시간대입니다."),

    // 파일 관련
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    // 공통
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허가되지 않은 HTTP 메소드입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.")

	;

	private final HttpStatus httpStatus;
	private final String message;

}


