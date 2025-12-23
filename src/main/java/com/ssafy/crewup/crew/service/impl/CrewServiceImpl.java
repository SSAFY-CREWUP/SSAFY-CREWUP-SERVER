package com.ssafy.crewup.crew.service.impl;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.crew.service.CrewService;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.enums.CrewMemberStatus;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.user.User;
import com.ssafy.crewup.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {

	private final CrewMapper crewMapper;
	private final CrewMemberMapper crewMemberMapper;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public Long createCrew(CrewCreateRequest request, String imageUrl, Long userId) {
		// 1. 크루장 정보 및 초기 페이스 설정
		User leaderUser = userMapper.findById(userId);
		if (leaderUser == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);

		Double initialPace = 0.0;
		if (leaderUser.getAveragePace() != null) {
			try {
				initialPace = Double.parseDouble(leaderUser.getAveragePace());
			} catch (NumberFormatException e) {
				initialPace = 0.0;
			}
		}

		// 2. Crew 엔티티 빌드 (imageUrl 파라미터를 직접 사용)
		Crew crew = Crew.builder()
			.name(request.name())
			.region(request.region())
			.description(request.description())
			.crewImage(imageUrl) // S3 업로드 URL 또는 null
			.memberCount(1)
			.activityTime(request.activityTime())
			.ageGroup(request.ageGroup())
			.genderLimit(request.genderLimit())
			.averagePace(initialPace)
			.keywords(request.keywords())
			.build();

		crewMapper.insert(crew);

		// 3. 리더 등록
		LocalDateTime now = LocalDateTime.now();
		crewMemberMapper.insert(CrewMember.builder()
			.crewId(crew.getId())
			.userId(userId)
			.role(CrewMemberRole.LEADER)
			.status(CrewMemberStatus.ACCEPTED)
			.appliedAt(now)
			.joinedAt(now)
			.build());

		return crew.getId();
	}
}
