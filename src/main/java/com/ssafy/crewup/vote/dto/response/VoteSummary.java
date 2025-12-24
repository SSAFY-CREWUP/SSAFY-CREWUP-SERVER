package com.ssafy.crewup.vote.dto.response;

import java.time.LocalDateTime;

public record VoteSummary(
	Long voteId,
	String title,
	LocalDateTime endAt,
	Boolean isClosed,
	Integer limitCount,
	Integer participantCount,
	Boolean multipleChoice,
	Boolean isAnonymous) {
}
