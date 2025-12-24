package com.ssafy.crewup.schedule.service.impl;

import com.ssafy.crewup.enums.ScheduleMemberStatus;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.schedule.Schedule;
import com.ssafy.crewup.schedule.ScheduleMember;
import com.ssafy.crewup.schedule.dto.request.ScheduleCreateRequest;
import com.ssafy.crewup.schedule.dto.request.ScheduleMemberStatusUpdateRequest;
import com.ssafy.crewup.schedule.dto.response.ScheduleCreatorCheckResponse;
import com.ssafy.crewup.schedule.dto.response.ScheduleGetResponse;
import com.ssafy.crewup.schedule.dto.response.ScheduleMemberResponse;
import com.ssafy.crewup.schedule.mapper.ScheduleMapper;
import com.ssafy.crewup.schedule.mapper.ScheduleMemberMapper;
import com.ssafy.crewup.schedule.service.ScheduleService;
import com.ssafy.crewup.user.User;
import com.ssafy.crewup.user.dto.response.UserResponse;
import com.ssafy.crewup.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본적으로 읽기 전용
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ScheduleMemberMapper scheduleMemberMapper;
    private final UserMapper userMapper;

    @Override
    public List<ScheduleGetResponse> getScheduleList(Long crewId) {
        List<Schedule> schedules = scheduleMapper.findByCrewId(crewId);
        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        return convertToScheduleGetResponses(schedules);
    }

    @Override
    public ScheduleGetResponse getScheduleDetail(Long scheduleId) {
        Schedule schedule = findScheduleOrThrow(scheduleId);
        List<ScheduleMemberResponse> memberResponses = getMemberResponses(scheduleId);

        return ScheduleGetResponse.from(schedule, memberResponses);
    }

    @Transactional
    @Override
    public Long createSchedule(ScheduleCreateRequest request, Long crewId, Long userId) {
        Schedule schedule = request.toEntity(crewId);
        scheduleMapper.insert(schedule);

        saveScheduleMember(schedule.getId(), userId, ScheduleMemberStatus.CONFIRMED);

        return schedule.getId();
    }

    @Transactional
    @Override
    public void joinSchedule(Long scheduleId, Long userId) {
        Schedule schedule = findScheduleOrThrow(scheduleId);

        validateDuplicateJoin(scheduleId, userId);
        validateScheduleCapacity(schedule);

        saveScheduleMember(scheduleId, userId, ScheduleMemberStatus.PENDING);

    }

    @Transactional
    @Override
    public void deleteSchedule(Long scheduleId, Long userId) {
        findScheduleOrThrow(scheduleId);
        validateCreator(scheduleId, userId);

        scheduleMemberMapper.deleteByScheduleId(scheduleId);
        scheduleMapper.delete(scheduleId);

    }

    @Override
    public ScheduleCreatorCheckResponse checkCreator(Long scheduleId, Long userId) {
        findScheduleOrThrow(scheduleId);
        ScheduleMember creator = findCreator(scheduleId);

        boolean isCreator = creator != null && creator.getUserId().equals(userId);
        return ScheduleCreatorCheckResponse.of(isCreator);
    }

    @Transactional
    @Override
    public void updateMemberStatus(Long scheduleId, Long userId, ScheduleMemberStatusUpdateRequest request) {
        findScheduleOrThrow(scheduleId);
        validateCreator(scheduleId, userId);

        ScheduleMember targetMember = findScheduleMemberOrThrow(request.getScheduleMemberId());
        validateMemberBelongsToSchedule(targetMember, scheduleId);

        scheduleMemberMapper.updateStatus(request.getScheduleMemberId(), request.getStatus());

    }

    // ==================== Helper Methods ====================

    /**
     * 스케줄 조회 (없으면 예외)
     */
    private Schedule findScheduleOrThrow(Long scheduleId) {
        Schedule schedule = scheduleMapper.findById(scheduleId);
        if (schedule == null) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
        return schedule;
    }

    /**
     * 스케줄 멤버 조회 (없으면 예외)
     */
    private ScheduleMember findScheduleMemberOrThrow(Long memberId) {
        ScheduleMember member = scheduleMemberMapper.findById(memberId);
        if (member == null) {
            throw new CustomException(ErrorCode.SCHEDULE_MEMBER_NOT_FOUND);
        }
        return member;
    }

    /**
     * 생성자 권한 검증
     */
    private void validateCreator(Long scheduleId, Long userId) {
        ScheduleMember creator = findCreator(scheduleId);
        if (creator == null || !creator.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_SCHEDULE_CREATOR);
        }
    }

    /**
     * 생성자 찾기 (CONFIRMED 상태 중 가장 먼저 생성된 사람)
     */
    private ScheduleMember findCreator(Long scheduleId) {
        List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(scheduleId);
        return members.stream()
                .filter(m -> m.getStatus() == ScheduleMemberStatus.CONFIRMED)
                .min(Comparator.comparing(ScheduleMember::getId))
                .orElse(null);
    }

    /**
     * 스케줄 멤버 저장
     */
    private void saveScheduleMember(Long scheduleId, Long userId, ScheduleMemberStatus status) {
        ScheduleMember member = ScheduleMember.builder()
                .scheduleId(scheduleId)
                .userId(userId)
                .status(status)
                .build();
        scheduleMemberMapper.insert(member);
    }

    /**
     * 중복 참가 검증
     */
    private void validateDuplicateJoin(Long scheduleId, Long userId) {
        int count = scheduleMemberMapper.countByScheduleIdAndUserId(scheduleId, userId);
        if (count > 0) {
            throw new CustomException(ErrorCode.ALREADY_JOINED);
        }
    }

    /**
     * 일정 정원 검증
     */
    private void validateScheduleCapacity(Schedule schedule) {
        List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(schedule.getId());
        if (members.size() >= schedule.getMaxPeople()) {
            throw new CustomException(ErrorCode.SCHEDULE_FULL);
        }
    }

    /**
     * 멤버가 해당 스케줄에 속하는지 검증
     */
    private void validateMemberBelongsToSchedule(ScheduleMember member, Long scheduleId) {
        if (!member.getScheduleId().equals(scheduleId)) {
            throw new CustomException(ErrorCode.INVALID_SCHEDULE_MEMBER);
        }
    }

    /**
     * 스케줄 목록을 응답 DTO로 변환
     */
    private List<ScheduleGetResponse> convertToScheduleGetResponses(List<Schedule> schedules) {
        List<Long> scheduleIds = schedules.stream()
                .map(Schedule::getId)
                .collect(Collectors.toList());

        List<ScheduleMember> allMembers = scheduleMemberMapper.findByScheduleIds(scheduleIds);
        if (allMembers.isEmpty()) {
            return schedules.stream()
                    .map(s -> ScheduleGetResponse.from(s, List.of()))
                    .collect(Collectors.toList());
        }

        Map<Long, User> userMap = getUserMap(allMembers);
        Map<Long, List<ScheduleMember>> membersByScheduleId = allMembers.stream()
                .collect(Collectors.groupingBy(ScheduleMember::getScheduleId));

        return schedules.stream()
                .map(schedule -> {
                    List<ScheduleMember> members = membersByScheduleId.getOrDefault(
                            schedule.getId(), List.of()
                    );
                    List<ScheduleMemberResponse> responses = members.stream()
                            .map(m -> ScheduleMemberResponse.of(
                                    m,
                                    UserResponse.from(userMap.get(m.getUserId()))
                            ))
                            .collect(Collectors.toList());
                    return ScheduleGetResponse.from(schedule, responses);
                })
                .collect(Collectors.toList());
    }

    /**
     * 멤버 응답 생성
     */
    private List<ScheduleMemberResponse> getMemberResponses(Long scheduleId) {
        List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(scheduleId);
        if (members.isEmpty()) {
            return List.of();
        }

        Map<Long, User> userMap = getUserMap(members);

        return members.stream()
                .map(member -> {
                    User user = userMap.get(member.getUserId());
                    UserResponse userResponse = UserResponse.from(user);
                    return ScheduleMemberResponse.of(member, userResponse);
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID로 User Map 생성
     */
    private Map<Long, User> getUserMap(List<ScheduleMember> members) {
        List<Long> userIds = members.stream()
                .map(ScheduleMember::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<User> users = userMapper.findByIds(userIds);
        return users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }
}
