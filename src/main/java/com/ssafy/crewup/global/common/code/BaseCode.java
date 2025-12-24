package com.ssafy.crewup.global.common.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
	HttpStatus getHttpStatus();
	String getMessage();
}
