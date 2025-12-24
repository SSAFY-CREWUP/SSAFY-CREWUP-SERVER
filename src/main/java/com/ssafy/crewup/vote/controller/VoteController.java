package com.ssafy.crewup.vote.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.vote.dto.request.VoteCastRequest;
import com.ssafy.crewup.vote.dto.request.VoteCreateRequest;
import com.ssafy.crewup.vote.dto.response.VoteListContainer;
import com.ssafy.crewup.vote.dto.response.VoteResponse;
import com.ssafy.crewup.vote.dto.response.VoteResultResponse;
import com.ssafy.crewup.vote.service.VoteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VoteController {
	private final VoteService voteService;

	// 크루 내 투표 생성
	@PostMapping("/crew/{crewId}/votes")
	public ResponseEntity<ApiResponseBody<Void>> createVote(
		@PathVariable Long crewId,
		@Valid @RequestBody VoteCreateRequest request,
		HttpSession session) {
		Long userId = getUserId(session);
		voteService.createVote(userId, crewId, request);
		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.CREATED));
	}

	// 투표 하기
	@PostMapping("/vote/{voteId}/cast")
	public ResponseEntity<ApiResponseBody<Void>> castVote(
		@PathVariable Long voteId,
		@Valid @RequestBody VoteCastRequest request,
		HttpSession session) {
		Long userId = getUserId(session);
		voteService.castVote(userId, voteId, request.optionIds());
		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.OK));
	}

	// 투표 상세 및 결과 조회
	@GetMapping("/vote/{voteId}/results")
	public ResponseEntity<ApiResponseBody<VoteResultResponse>> getResults(
		@PathVariable Long voteId,
		HttpSession session) {
		Long userId = getUserId(session);
		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.OK, voteService.getVoteResult(userId, voteId)));
	}

	private Long getUserId(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
		return userId;
	}

	@GetMapping("/crew/{crewId}/votes")
	public ResponseEntity<ApiResponseBody<VoteListContainer>> getVoteList(
		@PathVariable Long crewId,
		HttpSession session) {

		// 1. 세션 체크 (로그인 여부 확인)
		getUserId(session);

		// 2. 서비스 호출 (진행 중인 투표와 종료된 투표를 각각 가져옴)
		List<VoteResponse> activeVotes = voteService.getActiveVotes(crewId);
		List<VoteResponse> endedVotes = voteService.getEndedVotes(crewId);

		// 3. 응답 DTO에 담아서 반환
		VoteListContainer response = new VoteListContainer(activeVotes, endedVotes);

		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.OK, response));
	}
}
