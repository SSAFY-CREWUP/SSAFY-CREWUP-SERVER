package com.ssafy.crewup.crew.dto.request;

public record CrewSearchRequest(
	String search,       // 크루명 검색어
	String region,       // 지역 (예: 서울_강남구)
	java.util.List<String> activityTimes, // 오전, 점심, 저녁, 야간 (다중 선택)
	java.util.List<String> genderLimits,  // 모두, 남성, 여성 (다중 선택)
	java.util.List<String> ageGroups,     // 전연령, 2030 등 (다중 선택)
	Double minPace,      // 최소 페이스
	Double maxPace,      // 최대 페이스
	String sort,         // 정렬 기준: CREATED_AT, MEMBER_COUNT, AVERAGE_PACE
	String order         // 정렬 방향: DESC, ASC
) {}
