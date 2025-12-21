package com.ssafy.crewup.global.common.exception;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponseBody<Void>> handleGeneralException(CustomException e) {
		return ResponseEntity
			.status(e.getErrorStatus().getHttpStatus())
			.body(ApiResponseBody.onFailure(e.getErrorStatus()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponseBody<Void>> handleValidationException(MethodArgumentNotValidException e) {
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
			.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
			.collect(Collectors.joining(", "));

		return ResponseEntity
			.status(ErrorCode.BAD_REQUEST.getHttpStatus())
			.body(ApiResponseBody.onFailure(ErrorCode.BAD_REQUEST, errorMessage));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponseBody<Void>> handleException(Exception e) {
		return ResponseEntity
			.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
			.body(ApiResponseBody.onFailure(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
