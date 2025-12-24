package com.ssafy.crewup.vote.dto.response;

import java.util.List;

public record VoteResultResponse(
	Long voteId,
	String title,
	Boolean isAnonymous,
	List<OptionDetail> options
) {
	public record OptionDetail(
		Long optionId,
		String content,
		Integer count,
		List<VoterInfo> voters
	) {}

	public record VoterInfo(
		String nickname,
		String profileImage,
		String votedAt
	) {}
}
