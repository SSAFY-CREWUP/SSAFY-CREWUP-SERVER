package com.ssafy.crewup.vote.service.impl;

import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.vote.Vote;
import com.ssafy.crewup.vote.VoteOption;
import com.ssafy.crewup.vote.VoteRecord;
import com.ssafy.crewup.vote.dto.request.VoteCreateRequest;
import com.ssafy.crewup.vote.dto.response.VoteResponse;
import com.ssafy.crewup.vote.dto.response.VoteResultResponse;
import com.ssafy.crewup.vote.mapper.VoteMapper;
import com.ssafy.crewup.vote.mapper.VoteOptionMapper;
import com.ssafy.crewup.vote.mapper.VoteRecordMapper;
import com.ssafy.crewup.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.ssafy.crewup.vote.dto.response.VoteOptionResponse;
import com.ssafy.crewup.vote.dto.response.VoteSummary;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

	private final VoteMapper voteMapper;
	private final VoteOptionMapper voteOptionMapper;
	private final VoteRecordMapper voteRecordMapper;
	private final CrewMemberMapper crewMemberMapper;

	@Override
	@Transactional
	public void createVote(Long userId, Long crewId, VoteCreateRequest request) {
		// 1. 매니저 권한 검증
		validateManagerAuthority(userId, crewId);

		// 2. 투표 본체 생성
		Vote vote = Vote.builder()
			.crewId(crewId)
			.creatorId(userId)
			.title(request.title())
			.endAt(request.endAt())
			.multipleChoice(Objects.requireNonNullElse(request.multipleChoice(), false))
			.isAnonymous(Objects.requireNonNullElse(request.isAnonymous(), false))
			.limitCount(Objects.requireNonNullElse(request.limitCount(), 0))
			.build();
		voteMapper.insert(vote);

		// 3. 투표 선택지 생성 (최대 5개 제한은 컨트롤러 @Size로 검증)
		for (String content : request.options()) {
			voteOptionMapper.insert(VoteOption.builder()
				.voteId(vote.getId())
				.content(content)
				.count(0)
				.build());
		}
	}

	@Override
	@Transactional
	public void castVote(Long userId, Long voteId, List<Long> optionIds) {
		Vote vote = voteMapper.findById(voteId);
		if (vote == null)
			throw new CustomException(ErrorCode.NOT_FOUND);

		// 마감 시간 확인
		if (vote.getEndAt() != null && vote.getEndAt().isBefore(LocalDateTime.now())) {
			throw new CustomException(ErrorCode.BAD_REQUEST); // 마감된 투표
		}

		// 중복 선택 여부 확인
		if (!vote.getMultipleChoice() && optionIds.size() > 1) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		for (Long optionId : optionIds) {
			// 비관적 락 적용 (FOR UPDATE)
			VoteOption option = voteOptionMapper.findByIdWithLock(optionId);

			// 인원 제한 확인 (선착순)
			if (vote.getLimitCount() != null && vote.getLimitCount() > 0 && option.getCount() >= vote.getLimitCount()) {
				throw new CustomException(ErrorCode.BAD_REQUEST); // "인원 제한 초과"
			}

			// 투표 기록 삽입 (DB Unique 제약조건으로 1인 1투표/옵션 보장)
			VoteRecord record = VoteRecord.builder()
				.userId(userId)
				.voteId(voteId)
				.voteOptionId(optionId)
				.build();
			voteRecordMapper.insert(record);

			// 카운트 증가
			voteOptionMapper.incrementCount(optionId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public VoteResultResponse getVoteResult(Long userId, Long voteId) {
		Vote vote = voteMapper.findById(voteId);
		if (vote == null)
			throw new CustomException(ErrorCode.NOT_FOUND);

		// 투표 참여자만 결과 조회 가능
		List<VoteRecord> userRecords = voteRecordMapper.findByUserId(userId);
		boolean participated = userRecords.stream().anyMatch(r -> r.getVoteId().equals(voteId));
		if (!participated) {
			throw new CustomException(ErrorCode.FORBIDDEN); // 투표를 해야 결과를 볼 수 있음
		}

		List<VoteOption> options = voteOptionMapper.findByVoteId(voteId);
		List<VoteResultResponse.OptionDetail> optionDetails = options.stream().map(option -> {
			// 무기명 투표가 아닐 경우에만 투표자 명단 포함 (시간 순 정렬)
			List<VoteResultResponse.VoterInfo> voters = vote.getIsAnonymous() ? List.of()
				: voteRecordMapper.findVotersByOptionId(option.getId());

			return new VoteResultResponse.OptionDetail(
				option.getId(),
				option.getContent(),
				option.getCount(),
				voters);
		}).collect(Collectors.toList());

		return new VoteResultResponse(
			vote.getId(),
			vote.getTitle(),
			vote.getIsAnonymous(),
			optionDetails);
	}

	@Override
	@Transactional(readOnly = true)
	public List<VoteResponse> getActiveVotes(Long crewId) {
		List<VoteSummary> summaries = voteMapper.findActiveVotes(crewId);
		return mapToVoteResponse(summaries);
	}

	@Override
	@Transactional(readOnly = true)
	public List<VoteResponse> getEndedVotes(Long crewId) {
		List<VoteSummary> summaries = voteMapper.findEndedVotes(crewId);
		return mapToVoteResponse(summaries);
	}

	private List<VoteResponse> mapToVoteResponse(List<VoteSummary> summaries) {
		if (summaries.isEmpty())
			return new ArrayList<>();

		List<Long> voteIds = summaries.stream().map(VoteSummary::voteId).toList();

		// N+1 fetch for safety
		List<VoteOption> allOptions = new ArrayList<>();
		for (Long id : voteIds) {
			allOptions.addAll(voteOptionMapper.findByVoteId(id));
		}

		Map<Long, List<VoteOptionResponse>> optionsMap = allOptions.stream()
			.collect(Collectors.groupingBy(VoteOption::getVoteId,
				Collectors.mapping(opt -> new VoteOptionResponse(opt.getId(), opt.getContent()),
					Collectors.toList())));

		return summaries.stream().map(s -> new VoteResponse(
			s.voteId(),
			s.title(),
			s.endAt(),
			s.isClosed(),
			s.limitCount(),
			s.participantCount(),
			s.multipleChoice(),
			s.isAnonymous(),
			optionsMap.getOrDefault(s.voteId(), new ArrayList<>()))).toList();
	}

	@Override
	@Transactional
	public void closeVote(Long userId, Long voteId) {
		Vote vote = voteMapper.findById(voteId);
		if (!vote.getCreatorId().equals(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}
		voteMapper.closeVote(voteId);
	}

	@Override
	@Transactional
	public void deleteVote(Long userId, Long voteId) {
		Vote vote = voteMapper.findById(voteId);
		if (vote == null)
			throw new CustomException(ErrorCode.NOT_FOUND);

		// 작성자만 삭제 가능
		if (!vote.getCreatorId().equals(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}
		voteMapper.delete(voteId);
	}

	/**
	 * 권한 검증 헬퍼 메서드
	 */
	private void validateManagerAuthority(Long userId, Long crewId) {
		List<CrewMember> members = crewMemberMapper.findByCrewId(crewId);

		CrewMember currentMember = members.stream()
			.filter(m -> java.util.Objects.equals(m.getUserId(), userId))
			.findFirst()
			.orElseThrow(() -> new com.ssafy.crewup.global.common.exception.CustomException(
				com.ssafy.crewup.global.common.code.ErrorCode.UNAUTHORIZED));

		if (currentMember.getRole() == com.ssafy.crewup.enums.CrewMemberRole.MEMBER) {
			throw new com.ssafy.crewup.global.common.exception.CustomException(
				com.ssafy.crewup.global.common.code.ErrorCode.FORBIDDEN);
		}
	}
}
