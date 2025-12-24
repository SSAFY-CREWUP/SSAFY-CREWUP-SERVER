package com.ssafy.crewup.crew.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.dto.response.CrewDetailResponse;
import com.ssafy.crewup.crew.dto.response.CrewMemberDetailResponse;
import com.ssafy.crewup.crew.dto.request.CrewSearchRequest;
import com.ssafy.crewup.crew.dto.response.CrewListResponse;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.crew.service.CrewService;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.enums.CrewMemberStatus;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.user.User;
import com.ssafy.crewup.user.mapper.UserMapper;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import com.ssafy.crewup.crew.dto.response.CrewMemberListResponse;

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
		if (leaderUser == null)
			throw new CustomException(ErrorCode.USER_NOT_FOUND);

		Double initialPace = 0.0;
		if (leaderUser.getAveragePace() != null && !leaderUser.getAveragePace().isEmpty()) {
			try {
				System.out.println("User Average Pace Raw: " + leaderUser.getAveragePace());
				if (leaderUser.getAveragePace().contains(":")) {
					String[] parts = leaderUser.getAveragePace().split(":");
					if (parts.length == 2) {
						double minutes = Double.parseDouble(parts[0]);
						double seconds = Double.parseDouble(parts[1]);
						// Store as MM.SS for simple visual handling (e.g. 5:30 -> 5.30)
						initialPace = minutes + (seconds / 100.0);
					}
				} else {
					initialPace = Double.parseDouble(leaderUser.getAveragePace());
				}
				System.out.println("Calculated Initial Pace: " + initialPace);
			} catch (NumberFormatException e) {
				System.out.println("Failed to parse pace: " + e.getMessage());
				initialPace = 0.0;
			}
		} else {
			System.out.println("User Average Pace is null or empty");
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

	@Override
	@Transactional(readOnly = true)
	public CrewDetailResponse getCrewDetail(Long crewId) {
		// 1. 크루 정보 조회
		Crew crew = crewMapper.findById(crewId);
		if (crew == null) {
			throw new CustomException(ErrorCode.NOT_FOUND);
		}

		// 2. 크루 멤버 정보 조회
		List<CrewMemberDetailResponse> members = crewMapper.findMembersByCrewId(crewId);

		// 3. Response DTO로 변환
		return new CrewDetailResponse(
			crew.getId(),
			crew.getName(),
			crew.getDescription(),
			crew.getCrewImage(),
			crew.getRegion(),
			crew.getMemberCount(),
			crew.getActivityTime(),
			crew.getAgeGroup(),
			crew.getGenderLimit(),
			crew.getAveragePace(),
			crew.getKeywords(),
			members);
	}

	@Override
	@Transactional
	public void joinCrew(Long crewId, Long userId) {
		// 1. 크루 존재 여부 확인
		if (crewMapper.findById(crewId) == null) {
			throw new CustomException(ErrorCode.CREW_NOT_FOUND);
		}

		// 2. 이미 가입 신청했거나 멤버인지 확인
		CrewMember existingMember = crewMemberMapper.findByCrewIdAndUserId(crewId, userId);

		if (existingMember != null) {
			// 이미 가입된 상태(ACCEPTED)이거나 대기 중(WAITING)인 경우 모두 예외 처리
			throw new CustomException(ErrorCode.ALREADY_JOINED_OR_APPLIED);
		}

		// 3. 새로운 가입 신청 데이터 생성
		// Role은 일반 MEMBER로, 상태는 WAITING으로 설정합니다.
		CrewMember newJoinRequest = CrewMember.builder()
			.crewId(crewId)
			.userId(userId)
			.role(CrewMemberRole.MEMBER)
			.status(CrewMemberStatus.WAITING)
			.appliedAt(LocalDateTime.now())
			.build();

		// 4. DB 저장
		crewMemberMapper.insert(newJoinRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CrewListResponse> searchCrews(CrewSearchRequest request) {
		return crewMapper.searchCrews(request);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CrewListResponse> getMyCrews(Long userId) {
		return crewMapper.findCrewsByUserId(userId);
	}
    /**
     * 크루 멤버 리스트 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CrewMemberListResponse> getCrewMemberList(Long crewId) {
        validateCrewExists(crewId);

        List<CrewMember> crewMembers = crewMemberMapper.findAcceptedMembersByCrewId(crewId);
        if (crewMembers.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, User> userMap = fetchUserMapForMembers(crewMembers);
        List<CrewMemberListResponse> responses = buildMemberListResponses(crewMembers, userMap);

        return sortMembersByRoleAndJoinDate(responses);
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long crewId, Long memberId, CrewMemberStatus status, Long requestUserId) {
        validateCrewExists(crewId);
        validateLeaderOrManager(crewId, requestUserId);

        CrewMember member = findMemberById(memberId);
        validateMemberBelongsToCrew(member, crewId);

        CrewMemberStatus previousStatus = member.getStatus();
        updateMemberStatusAndJoinedAt(member, status);

        // WAITING → ACCEPTED인 경우 크루 멤버 수 증가
        if (shouldIncrementMemberCount(previousStatus, status)) {
            incrementCrewMemberCount(crewId);
        }
    }


    /**
     * 크루 존재 여부 검증
     */
    private void validateCrewExists(Long crewId) {
        if (crewMapper.findById(crewId) == null) {
            throw new CustomException(ErrorCode.CREW_NOT_FOUND);
        }
    }

    /**
     * 크루 멤버들의 사용자 정보 조회 및 Map 생성
     */
    private Map<Long, User> fetchUserMapForMembers(List<CrewMember> crewMembers) {
        List<Long> userIds = crewMembers.stream()
                .map(CrewMember::getUserId)
                .collect(Collectors.toList());

        List<User> users = userMapper.findByIds(userIds);

        return users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    /**
     * 멤버 리스트 응답 DTO 생성
     */
    private List<CrewMemberListResponse> buildMemberListResponses(
            List<CrewMember> crewMembers,
            Map<Long, User> userMap) {

        return crewMembers.stream()
                .map(member -> {
                    User user = userMap.get(member.getUserId());
                    return CrewMemberListResponse.of(member, user);
                })
                .collect(Collectors.toList());
    }

    /**
     * 권한 우선순위와 가입일 기준 정렬
     */
    private List<CrewMemberListResponse> sortMembersByRoleAndJoinDate(
            List<CrewMemberListResponse> responses) {

        responses.sort(Comparator
                .comparing((CrewMemberListResponse r) -> getRolePriority(r.getRole()))
                .thenComparing(CrewMemberListResponse::getJoinedAt));

        return responses;
    }

    /**
     * 권한 우선순위 (LEADER > MANAGER > MEMBER)
     */
    private int getRolePriority(CrewMemberRole role) {
        switch (role) {
            case LEADER:
                return 1;
            case MANAGER:
                return 2;
            case MEMBER:
                return 3;
            default:
                return 4;
        }
    }
    /**
     * 리더 또는 매니저 권한 검증
     */
    private void validateLeaderOrManager(Long crewId, Long userId) {
        CrewMember requestMember = crewMemberMapper.findByCrewIdAndUserId(crewId, userId);

        if (requestMember == null) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (requestMember.getRole() == CrewMemberRole.MEMBER) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 멤버 조회
     */
    private CrewMember findMemberById(Long memberId) {
        CrewMember member = crewMemberMapper.findById(memberId);

        if (member == null) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        return member;
    }

    /**
     * 멤버가 해당 크루에 속하는지 검증
     */
    private void validateMemberBelongsToCrew(CrewMember member, Long crewId) {
        if (!member.getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 멤버 상태 및 가입일 업데이트
     */
    private void updateMemberStatusAndJoinedAt(CrewMember member, CrewMemberStatus status) {
        member.setStatus(status);

        // ACCEPTED로 변경 시 가입일 설정
        if (isAcceptedAndJoinDateNull(status, member.getJoinedAt())) {
            member.setJoinedAt(LocalDateTime.now());
        }

        crewMemberMapper.update(member);
    }

    /**
     * ACCEPTED 상태이고 가입일이 null인지 확인
     */
    private boolean isAcceptedAndJoinDateNull(CrewMemberStatus status, LocalDateTime joinedAt) {
        return status == CrewMemberStatus.ACCEPTED && joinedAt == null;
    }

    /**
     * 멤버 수를 증가시켜야 하는지 확인
     */
    private boolean shouldIncrementMemberCount(CrewMemberStatus previousStatus, CrewMemberStatus newStatus) {
        return previousStatus == CrewMemberStatus.WAITING && newStatus == CrewMemberStatus.ACCEPTED;
    }

    /**
     * 크루 멤버 수 증가
     */
    private void incrementCrewMemberCount(Long crewId) {
        Crew crew = crewMapper.findById(crewId);
        crew.setMemberCount(crew.getMemberCount() + 1);
        crewMapper.update(crew);
    }
}
