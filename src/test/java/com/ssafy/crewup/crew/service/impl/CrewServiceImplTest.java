package com.ssafy.crewup.crew.service.impl;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.global.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

class CrewServiceImplTest {

    @Mock
    private CrewMapper crewMapper;

    @Mock
    private CrewMemberMapper crewMemberMapper;

    @InjectMocks
    private CrewServiceImpl crewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CrewCreateRequest buildValidRequest() {
        return new CrewCreateRequest(
            "강남 새벽 러닝",
            "서울 강남구",
            "상쾌한 아침을 여는 강남 러닝 크루입니다.",
            "오전",
            "2030",
            "모두",
            "https://crewup-s3.ssafy.io/images/crew_01.png",
            List.of("건강", "친목", "미라클모닝")
        );
    }

    @Test
    @DisplayName("크루 생성 성공 시 crew와 leader가 저장되고 crewId 반환")
    void createCrew_success() {
        CrewCreateRequest req = buildValidRequest();

        doAnswer(invocation -> {
            Crew arg = invocation.getArgument(0);
            arg.setId(100L);
            return 1;
        }).when(crewMapper).insert(any(Crew.class));

        Long result = crewService.createCrew(req, 1L);

        assertEquals(100L, result);

        ArgumentCaptor<CrewMember> cmCaptor = ArgumentCaptor.forClass(CrewMember.class);
        verify(crewMemberMapper).insert(cmCaptor.capture());
        CrewMember saved = cmCaptor.getValue();
        assertEquals(100L, saved.getCrewId());
        assertEquals(1L, saved.getUserId());
        assertNotNull(saved.getAppliedAt());
        assertNotNull(saved.getJoinedAt());
    }

    @Test
    @DisplayName("유효하지 않은 지역이면 INVALID_REGION 발생")
    void createCrew_invalidRegion() {
        CrewCreateRequest req = buildValidRequest();
        CrewCreateRequest invalid = new CrewCreateRequest(
            req.name(),
            "서울 가짜구",
            req.description(),
            req.activityTime(),
            req.ageGroup(),
            req.genderLimit(),
            req.crewImage(),
            req.keywords()
        );
        assertThrows(CustomException.class, () -> crewService.createCrew(invalid, 1L));
    }

    @Test
    @DisplayName("유효하지 않은 활동 시간대면 INVALID_ACTIVITY_TIME 발생")
    void createCrew_invalidActivityTime() {
        CrewCreateRequest req = buildValidRequest();
        CrewCreateRequest invalid = new CrewCreateRequest(
            req.name(),
            req.region(),
            req.description(),
            "아침",
            req.ageGroup(),
            req.genderLimit(),
            req.crewImage(),
            req.keywords()
        );
        assertThrows(CustomException.class, () -> crewService.createCrew(invalid, 1L));
    }
}
