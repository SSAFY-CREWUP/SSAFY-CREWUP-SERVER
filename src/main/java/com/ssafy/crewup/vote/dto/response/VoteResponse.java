package com.ssafy.crewup.vote.dto.response;

import java.time.LocalDateTime;

public record VoteResponse(
	Long voteId,
	String title,
	LocalDateTime endAt,
	Boolean isClosed,
	Integer limitCount,
	Integer participantCount) {
}
