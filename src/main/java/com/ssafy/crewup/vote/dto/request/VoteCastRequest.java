package com.ssafy.crewup.vote.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VoteCastRequest(
	@NotNull(message = "투표 옵션은 필수입니다.")
	@NotEmpty(message = "최소 한 개 이상의 옵션을 선택해야 합니다.")
	@Size(min = 1) List<Long> optionIds
) {}
