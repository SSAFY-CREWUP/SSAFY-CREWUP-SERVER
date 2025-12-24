package com.ssafy.crewup.vote.dto.response;

import java.util.List;

public record VoteListContainer(
	List<VoteResponse> activeVotes,
	List<VoteResponse> endedVotes
) {}