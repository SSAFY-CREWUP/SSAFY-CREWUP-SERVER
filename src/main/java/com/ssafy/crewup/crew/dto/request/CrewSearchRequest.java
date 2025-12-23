package com.ssafy.crewup.crew.dto.request;

public record CrewSearchRequest(
	String search,       // 크루명 검색어
	String region,       // 지역 (예: 서울_강남구)
	String activityTime, // 오전, 점심, 저녁, 야간
	String genderLimit,  // 모두, 남성, 여성
	String ageGroup,     // 전연령, 2030 등
	String sort,         // 정렬 기준: CREATED_AT, MEMBER_COUNT, AVERAGE_PACE
	String order         // 정렬 방향: DESC, ASC
) {}
