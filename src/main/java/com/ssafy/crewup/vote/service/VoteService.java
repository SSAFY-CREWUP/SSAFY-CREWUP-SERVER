package com.ssafy.crewup.vote.service;

import java.util.List;

import com.ssafy.crewup.vote.dto.request.VoteCreateRequest;
import com.ssafy.crewup.vote.dto.response.VoteResultResponse;
import com.ssafy.crewup.vote.dto.response.VoteResponse;

public interface VoteService {
	// 투표 생성 (매니저 이상)
	void createVote(Long userId, Long crewId, VoteCreateRequest request);

	// 투표 하기 (중복 선택 포함)
	void castVote(Long userId, Long voteId, List<Long> optionIds);

	// 투표 결과 및 상세 조회 (참여자 전용)
	VoteResultResponse getVoteResult(Long userId, Long voteId);

	// 진행 중인 투표 목록 조회
	List<VoteResponse> getActiveVotes(Long crewId);

	// 종료된 투표 목록 조회
	List<VoteResponse> getEndedVotes(Long crewId);

	// 투표 강제 종료
	void closeVote(Long userId, Long voteId);

	// 투표 삭제
	void deleteVote(Long userId, Long voteId);
}
