package com.ssafy.crewup.crew.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.vote.Vote;
import com.ssafy.crewup.vote.VoteOption;
import com.ssafy.crewup.vote.dto.request.VoteCreateRequest;
import com.ssafy.crewup.vote.mapper.VoteMapper;
import com.ssafy.crewup.vote.mapper.VoteOptionMapper;
import com.ssafy.crewup.vote.mapper.VoteRecordMapper;
import com.ssafy.crewup.vote.service.impl.VoteServiceImpl;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

	@InjectMocks
	private VoteServiceImpl voteService;

	@Mock
	private VoteMapper voteMapper;
	@Mock
	private VoteOptionMapper voteOptionMapper;
	@Mock
	private VoteRecordMapper voteRecordMapper;
	@Mock
	private CrewMemberMapper crewMemberMapper;

	@Test
	@DisplayName("투표 생성 성공 - 매니저 권한일 때")
	void createVote_Success() {
		// given
		Long userId = 1L;
		Long crewId = 10L;
		VoteCreateRequest request = new VoteCreateRequest(
			"회식 장소", LocalDateTime.now().plusDays(1), false, false, 10, List.of("강남", "홍대")
		);

		// 권한 모킹: 매니저로 설정
		CrewMember manager = CrewMember.builder().userId(userId).role(CrewMemberRole.MANAGER).build();
		given(crewMemberMapper.findByCrewId(crewId)).willReturn(List.of(manager));

		// when
		voteService.createVote(userId, crewId, request);

		// then
		verify(voteMapper, times(1)).insert(any(Vote.class));
		verify(voteOptionMapper, times(2)).insert(any(VoteOption.class));
	}

	@Test
	@DisplayName("투표 생성 실패 - 일반 멤버 권한일 때")
	void createVote_Fail_Forbidden() {
		// given
		Long userId = 1L;
		Long crewId = 10L;
		VoteCreateRequest request = new VoteCreateRequest("제목", null, false, false, 0, List.of("A", "B"));

		CrewMember member = CrewMember.builder().userId(userId).role(CrewMemberRole.MEMBER).build();
		given(crewMemberMapper.findByCrewId(crewId)).willReturn(List.of(member));

		// when & then
		assertThatThrownBy(() -> voteService.createVote(userId, crewId, request))
			.isInstanceOf(CustomException.class)
			.extracting("errorStatus")
			.isEqualTo(ErrorCode.FORBIDDEN);
	}

	@Test
	@DisplayName("투표하기 실패 - 인원 제한 초과 시")
	void castVote_Fail_LimitExceeded() {
		// given
		Long userId = 1L;
		Long voteId = 100L;
		Long optionId = 1000L;

		Vote vote = Vote.builder()
			.id(voteId)
			.limitCount(5)
			.multipleChoice(false)
			.isAnonymous(false)
			.endAt(LocalDateTime.now().plusHours(1))
			.build();

		VoteOption option = VoteOption.builder()
			.id(optionId)
			.count(5) // 이미 꽉 찬 상태
			.build();

		given(voteMapper.findById(voteId)).willReturn(vote);
		given(voteOptionMapper.findByIdWithLock(optionId)).willReturn(option);

		// when & then
		assertThatThrownBy(() -> voteService.castVote(userId, voteId, List.of(optionId)))
			.isInstanceOf(CustomException.class);
	}
}
