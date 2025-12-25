package com.ssafy.crewup.crew.dto.response;

import java.util.List;

public record CrewListResponse(
	Long crewId,
	String name,
	String region,
	Integer memberCount,
	String activityTime,
	Double averagePace,
	String ageGroup,
	String genderLimit,
	String crewImage,
	List<String> keywords,
	Integer matchScore // 매칭 점수
) {
}
