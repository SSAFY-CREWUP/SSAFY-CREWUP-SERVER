package com.ssafy.crewup.schedule.service.impl;

import com.ssafy.crewup.enums.ScheduleMemberStatus;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.schedule.Schedule;
import com.ssafy.crewup.schedule.ScheduleMember;
import com.ssafy.crewup.schedule.dto.request.ScheduleCreateRequest;
import com.ssafy.crewup.schedule.dto.response.ScheduleGetResponse;
import com.ssafy.crewup.schedule.dto.response.ScheduleMemberResponse;
import com.ssafy.crewup.schedule.mapper.ScheduleMapper;
import com.ssafy.crewup.schedule.mapper.ScheduleMemberMapper;
import com.ssafy.crewup.schedule.service.ScheduleService;
import com.ssafy.crewup.user.User;
import com.ssafy.crewup.user.dto.response.UserResponse;
import com.ssafy.crewup.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ScheduleMemberMapper scheduleMemberMapper;
    private final UserMapper userMapper;

    @Override
    public List<ScheduleGetResponse> getScheduleList(Long crewId) {
        // 1. 스케줄 목록 조회
        List<Schedule> schedules = scheduleMapper.findByCrewId(crewId);

        if (schedules.isEmpty()) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        // 2. 모든 스케줄 ID 추출
        List<Long> scheduleIds = schedules.stream()
                .map(Schedule::getId)
                .collect(Collectors.toList());

        // 3. 모든 참가자를 한 번에 조회
        List<ScheduleMember> allMembers = scheduleMemberMapper.findByScheduleIds(scheduleIds);

        if (allMembers.isEmpty()) {
            return schedules.stream()
                    .map(schedule -> ScheduleGetResponse.from(schedule, List.of()))
                    .collect(Collectors.toList());
        }

        // 4. 모든 참가자의 userId 추출
        List<Long> userIds = allMembers.stream()
                .map(ScheduleMember::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 5. 사용자 정보를 한 번에 조회
        List<User> users = userMapper.findByIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 6. scheduleId를 기준으로 그룹핑
        Map<Long, List<ScheduleMember>> membersByScheduleId = allMembers.stream()
                .collect(Collectors.groupingBy(ScheduleMember::getScheduleId));

        // 7. 응답 생성
        return schedules.stream()
                .map(schedule -> {
                    List<ScheduleMember> members = membersByScheduleId.getOrDefault(
                            schedule.getId(),
                            List.of()
                    );

                    List<ScheduleMemberResponse> memberResponses = members.stream()
                            .map(member -> {
                                User user = userMap.get(member.getUserId());
                                UserResponse userSimple = UserResponse.from(user);
                                return ScheduleMemberResponse.of(member, userSimple);  // of 사용!
                            })
                            .collect(Collectors.toList());

                    return ScheduleGetResponse.from(schedule, memberResponses);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleGetResponse getScheduleDetail(Long scheduleId) {
        // 1. 스케줄 조회
        Schedule schedule = scheduleMapper.findById(scheduleId);

        if (schedule == null) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        // 2. 참가자 목록 조회
        List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(scheduleId);

        if (members.isEmpty()) {
            return ScheduleGetResponse.from(schedule, List.of());
        }

        // 3. 참가자들의 userId 추출
        List<Long> userIds = members.stream()
                .map(ScheduleMember::getUserId)
                .collect(Collectors.toList());

        // 4. 사용자 정보를 한 번에 조회
        List<User> users = userMapper.findByIds(userIds);

        // 5. userId를 key로 하는 Map 생성
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 6. ScheduleMemberResponse 생성 (사용자 정보 포함)
        List<ScheduleMemberResponse> memberResponses = members.stream()
                .map(member -> {
                    User user = userMap.get(member.getUserId());
                    UserResponse userSimple = UserResponse.from(user);
                    return ScheduleMemberResponse.of(member, userSimple);
                })
                .collect(Collectors.toList());

        return ScheduleGetResponse.from(schedule, memberResponses);
    }

    @Transactional
    @Override
    public Long createSchedule(ScheduleCreateRequest request, Long userId) {
        // 1. 일정 데이터 생성 및 INSERT
        Schedule schedule = request.toEntity();
        scheduleMapper.insert(schedule);

        // 2. 방장을 참여 멤버 테이블에 참석으로
        ScheduleMember leader = ScheduleMember.builder()
                .scheduleId(schedule.getId()) // 방금 생성된 ID 사용
                .userId(userId)               // 생성자 ID
                .status(ScheduleMemberStatus.CONFIRMED)
                .build();

        scheduleMemberMapper.insert(leader);

        return schedule.getId();
    }

    @Override
    @Transactional
    public void joinSchedule(Long scheduleId, Long userId) {
        try {
            // 1. 스케줄 존재 확인
            Schedule schedule = scheduleMapper.findById(scheduleId);

            if (schedule == null) {
                throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
            }

            // 2. 이미 참가 중인지 확인
            int count = scheduleMemberMapper.countByScheduleIdAndUserId(scheduleId, userId);

            if (count > 0) {
                throw new CustomException(ErrorCode.ALREADY_JOINED);
            }

            // 3. 최대 인원 확인
            List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(scheduleId);

            if (members.size() >= schedule.getMaxPeople()) {
                throw new CustomException(ErrorCode.SCHEDULE_FULL);
            }

            // 4. 참가 신청
            ScheduleMember scheduleMember = ScheduleMember.builder()
                    .scheduleId(scheduleId)
                    .userId(userId)
                    .status(ScheduleMemberStatus.PENDING) //가본값: 대기 설정
                    .build();
            int result = scheduleMemberMapper.insert(scheduleMember);


        } catch (Exception e) {
            throw e;
        }
    }
}
