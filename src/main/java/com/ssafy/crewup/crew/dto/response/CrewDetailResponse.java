package com.ssafy.crewup.crew.dto.response;

import java.util.List;

public record CrewDetailResponse(
	Long crewId,
	String name,
	String description,
	String crewImage,
	String region,
	Integer memberCount,
	String activityTime,
	String ageGroup,
	String genderLimit,
	Double averagePace,
	List<String> keywords,
	List<CrewMemberDetailResponse> members
) {}
