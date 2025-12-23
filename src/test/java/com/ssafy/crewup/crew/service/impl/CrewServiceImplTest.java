package com.ssafy.crewup.crew.service.impl;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.dto.response.CrewDetailResponse;
import com.ssafy.crewup.crew.dto.response.CrewMemberDetailResponse;
import com.ssafy.crewup.crew.dto.request.CrewSearchRequest;
import com.ssafy.crewup.crew.dto.response.CrewListResponse;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.enums.CrewMemberStatus;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.user.User;
import com.ssafy.crewup.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CrewServiceImplTest {

	@Mock
	private CrewMapper crewMapper;

	@Mock
	private CrewMemberMapper crewMemberMapper;

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private CrewServiceImpl crewService;

	private final String TEST_IMAGE_URL = "https://crewup-s3.ssafy.io/images/crew_01.png";
	private final Long TEST_LEADER_ID = 1L;
	private final Long TEST_CREW_ID = 100L;
	private final Long TEST_USER_ID = 2L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(userMapper.findById(anyLong())).thenReturn(
			User.builder()
				.id(TEST_LEADER_ID)
				.averagePace("5.5")
				.build()
		);
	}

	private CrewCreateRequest buildValidRequest() {
		return new CrewCreateRequest(
			"강남 새벽 러닝",
			"서울_강남구",
			"상쾌한 아침을 여는 강남 러닝 크루입니다.",
			"오전",
			"2030",
			"모두",
			List.of("건강", "친목", "미라클모닝")
		);
	}

	@Test
	@DisplayName("크루 생성 성공 시 crew와 leader가 저장되고 crewId 반환")
	void createCrew_success() {
		CrewCreateRequest req = buildValidRequest();
		when(crewMapper.insert(any(Crew.class))).thenAnswer(invocation -> {
			Crew crew = invocation.getArgument(0);
			crew.setId(TEST_CREW_ID);
			return 1;
		});

		// when
		Long result = crewService.createCrew(req, TEST_IMAGE_URL, TEST_LEADER_ID);

		assertEquals(TEST_CREW_ID, result);
		verify(crewMapper).insert(any(Crew.class));
		verify(crewMemberMapper).insert(any(CrewMember.class));

		// then
		assertEquals(expectedCrewId, result);

		// 크루 저장 데이터 검증
		ArgumentCaptor<Crew> crewCaptor = ArgumentCaptor.forClass(Crew.class);
		verify(crewMapper).insert(crewCaptor.capture());
		Crew savedCrew = crewCaptor.getValue();
		assertEquals(req.name(), savedCrew.getName());
		assertEquals(TEST_IMAGE_URL, savedCrew.getCrewImage());
		assertEquals(5.5, savedCrew.getAveragePace());

		// 크루장 등록 검증
		verify(crewMemberMapper).insert(any(CrewMember.class));
	}

	@Test
	@DisplayName("크루 목록 검색 시 필터 조건이 매퍼로 전달되는지 확인")
	void searchCrews_success() {
		// given
		CrewSearchRequest searchReq = new CrewSearchRequest(
			"러닝", "서울_강남구", "오전", "모두", "2030", "MEMBER_COUNT", "DESC"
		);
		List<CrewListResponse> expectedList = List.of(
			new CrewListResponse(100L, "강남 러닝", "서울_강남구", 10, "오전", 5.0, "2030", null, List.of("친목"))
		);

		when(crewMapper.searchCrews(any(CrewSearchRequest.class))).thenReturn(expectedList);

		// when
		List<CrewListResponse> result = crewService.searchCrews(searchReq);

		// then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("강남 러닝", result.get(0).name());
		verify(crewMapper).searchCrews(searchReq);
	}

	@Test
	@DisplayName("존재하지 않는 사용자로 크루 생성 시 USER_NOT_FOUND 예외 발생")
	void createCrew_userNotFound() {
		// given
		when(userMapper.findById(anyLong())).thenReturn(null);
		CrewCreateRequest req = buildValidRequest();

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> crewService.createCrew(req, TEST_IMAGE_URL, 999L));
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorStatus());
	}

	@Test
	@DisplayName("크루 상세 정보 조회 성공 시 멤버 리스트를 포함한 DTO 반환")
	void getCrewDetail_success() {
		// given
		Crew crew = Crew.builder()
			.id(TEST_CREW_ID)
			.name("강남 새벽 러닝")
			.region("서울_강남구")
			.keywords(List.of("러닝", "건강"))
			.build();

		List<CrewMemberDetailResponse> mockMembers = List.of(
			new CrewMemberDetailResponse(TEST_LEADER_ID, "리더닉네임", "profile.jpg", "LEADER"),
			new CrewMemberDetailResponse(TEST_USER_ID, "멤버닉네임", null, "MEMBER")
		);

		when(crewMapper.findById(TEST_CREW_ID)).thenReturn(crew);
		when(crewMapper.findMembersByCrewId(TEST_CREW_ID)).thenReturn(mockMembers);

		// when
		CrewDetailResponse result = crewService.getCrewDetail(TEST_CREW_ID);

		// then
		assertNotNull(result);
		assertEquals("강남 새벽 러닝", result.name());
		assertEquals(2, result.members().size());
		assertEquals("LEADER", result.members().get(0).role());
		verify(crewMapper).findById(TEST_CREW_ID);
		verify(crewMapper).findMembersByCrewId(TEST_CREW_ID);
	}

	@Test
	@DisplayName("가입 신청 성공 시 WAITING 상태의 멤버 데이터가 저장됨")
	void joinCrew_success() {
		// given
		when(crewMapper.findById(TEST_CREW_ID)).thenReturn(Crew.builder().id(TEST_CREW_ID).build());
		when(crewMemberMapper.findByCrewIdAndUserId(TEST_CREW_ID, TEST_USER_ID)).thenReturn(null);
		when(userMapper.findById(anyLong())).thenReturn(
			User.builder().id(TEST_LEADER_ID).averagePace(null).build()
		);

		CrewCreateRequest req = buildValidRequest();

		// when
		crewService.joinCrew(TEST_CREW_ID, TEST_USER_ID);

		// then
		ArgumentCaptor<CrewMember> captor = ArgumentCaptor.forClass(CrewMember.class);
		verify(crewMemberMapper).insert(captor.capture());

		CrewMember savedMember = captor.getValue();
		assertEquals(TEST_CREW_ID, savedMember.getCrewId());
		assertEquals(TEST_USER_ID, savedMember.getUserId());
		assertEquals(CrewMemberStatus.WAITING, savedMember.getStatus()); // WAITING 상태 검증
		assertEquals(CrewMemberRole.MEMBER, savedMember.getRole());
	}
}

	@Test
	@DisplayName("이미 가입 신청을 했거나 멤버인 경우 ALREADY_JOINED_OR_APPLIED 예외 발생")
	void joinCrew_fail_alreadyApplied() {
		// given
		when(crewMapper.findById(TEST_CREW_ID)).thenReturn(Crew.builder().id(TEST_CREW_ID).build());
		// 이미 존재하는 데이터 모킹
		when(crewMemberMapper.findByCrewIdAndUserId(TEST_CREW_ID, TEST_USER_ID))
			.thenReturn(CrewMember.builder().id(50L).build());

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> crewService.joinCrew(TEST_CREW_ID, TEST_USER_ID));

		assertEquals(ErrorCode.ALREADY_JOINED_OR_APPLIED, exception.getErrorStatus());
		verify(crewMemberMapper, never()).insert(any(CrewMember.class)); // 저장되지 않아야 함
	}

	@Test
	@DisplayName("존재하지 않는 크루에 가입 신청 시 CREW_NOT_FOUND 예외 발생")
	void joinCrew_fail_crewNotFound() {
		// given
		when(crewMapper.findById(TEST_CREW_ID)).thenReturn(null);

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> crewService.joinCrew(TEST_CREW_ID, TEST_USER_ID));

		assertEquals(ErrorCode.CREW_NOT_FOUND, exception.getErrorStatus());
	}
}