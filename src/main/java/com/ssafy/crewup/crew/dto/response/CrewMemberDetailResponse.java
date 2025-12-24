package com.ssafy.crewup.crew.dto.response;

public record CrewMemberDetailResponse(
	Long userId,
	String nickname,
	String profileImage,
	String role
) {}
