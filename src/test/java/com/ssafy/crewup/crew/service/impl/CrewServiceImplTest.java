package com.ssafy.crewup.crew.service.impl;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
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
		// given
		CrewCreateRequest req = buildValidRequest();
		Long expectedCrewId = 100L;

		when(crewMapper.insert(any(Crew.class))).thenAnswer(invocation -> {
			Crew crew = invocation.getArgument(0);
			crew.setId(expectedCrewId);
			return 1;
		});

		// when
		Long result = crewService.createCrew(req, TEST_IMAGE_URL, TEST_LEADER_ID);

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
	@DisplayName("평균 페이스가 없는 사용자로 크루 생성 시 기본 0.0으로 설정")
	void createCrew_userWithoutPace() {
		// given
		when(userMapper.findById(anyLong())).thenReturn(
			User.builder().id(TEST_LEADER_ID).averagePace(null).build()
		);

		CrewCreateRequest req = buildValidRequest();

		// when
		crewService.createCrew(req, TEST_IMAGE_URL, TEST_LEADER_ID);

		// then
		ArgumentCaptor<Crew> crewCaptor = ArgumentCaptor.forClass(Crew.class);
		verify(crewMapper).insert(crewCaptor.capture());
		assertEquals(0.0, crewCaptor.getValue().getAveragePace());
	}
}