package com.ssafy.crewup.vote.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VoteCreateRequest(
	@NotBlank String title,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime endAt,
	Boolean multipleChoice,
	Boolean isAnonymous,
	Integer limitCount,
	@Size(min = 2, max = 5) List<String> options
) {}
