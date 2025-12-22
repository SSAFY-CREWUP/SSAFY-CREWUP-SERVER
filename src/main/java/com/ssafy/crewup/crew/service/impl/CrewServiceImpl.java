package com.ssafy.crewup.crew.service.impl;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.crew.service.CrewService;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.enums.CrewMemberStatus;
import com.ssafy.crewup.enums.Region;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {

    private final CrewMapper crewMapper;
    private final CrewMemberMapper crewMemberMapper;

    @Override
    public Long createCrew(CrewCreateRequest request, Long userId) {
        if (!Region.isValidLabel(request.region())) {
            throw new CustomException(ErrorCode.INVALID_REGION);
        }

        String time = request.activityTime();
        if (!("오전".equals(time) || "오후".equals(time) || "저녁".equals(time) || "야간".equals(time))) {
            throw new CustomException(ErrorCode.INVALID_ACTIVITY_TIME);
        }

        Crew crew = Crew.builder()
            .name(request.name())
            .region(request.region())
            .description(request.description())
            .crewImage(request.crewImage())
            .memberCount(1)
            .build();

        crewMapper.insert(crew);

        LocalDateTime now = LocalDateTime.now();
        CrewMember leader = CrewMember.builder()
            .crewId(crew.getId())
            .userId(userId)
            .role(CrewMemberRole.LEADER)
            .status(CrewMemberStatus.ACCEPTED)
            .appliedAt(now)
            .joinedAt(now)
            .build();
        crewMemberMapper.insert(leader);

        return crew.getId();
    }
}
