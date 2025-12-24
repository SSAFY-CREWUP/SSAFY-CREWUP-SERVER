package com.ssafy.crewup.vote.dto.request;

import java.util.List;

import jakarta.validation.constraints.Size;

public record VoteCastRequest(
	@Size(min = 1) List<Long> optionIds
) {}
