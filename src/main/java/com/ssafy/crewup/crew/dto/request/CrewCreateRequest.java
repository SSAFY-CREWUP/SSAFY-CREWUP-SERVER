package com.ssafy.crewup.crew.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CrewCreateRequest(
	@NotBlank String name,
	@NotBlank String region,
	@NotBlank String description,
	@NotBlank String activityTime,
	@NotBlank String ageGroup,
	@NotBlank String genderLimit,
	@Size(max = 4) List<String> keywords // 최대 4개 제한
) {}
