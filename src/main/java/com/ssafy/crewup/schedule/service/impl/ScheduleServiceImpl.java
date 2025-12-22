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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ScheduleMemberMapper scheduleMemberMapper;

    @Override
    public List<ScheduleGetResponse> getScheduleList(Long crewId) {
        List<Schedule> schedules = scheduleMapper.findByCrewId(crewId);

        return schedules.stream()
                .map(schedule -> {
                    // 각 스케줄의 참가자 목록 조회
                    List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(schedule.getId());
                    List<ScheduleMemberResponse> memberResponses = members.stream()
                            .map(ScheduleMemberResponse::from)
                            .collect(Collectors.toList());

                    return ScheduleGetResponse.of(schedule, memberResponses);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleGetResponse getScheduleDetail(Long scheduleId) {
        Schedule schedule = scheduleMapper.findById(scheduleId);

        if (schedule == null) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        // 참가자 목록 조회
        List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(scheduleId);
        List<ScheduleMemberResponse> memberResponses = members.stream()
                .map(ScheduleMemberResponse::from)
                .collect(Collectors.toList());

        return ScheduleGetResponse.of(schedule, memberResponses);
    }

    @Transactional
    @Override
    public Long createSchedule(ScheduleCreateRequest request, Long userId) {
        // 1. 일정 데이터 생성 및 INSERT
        Schedule schedule = request.toEntity();
        scheduleMapper.insert(schedule);

        // scheduleMapper.insert가 실행된 후, schedule.getId()에는 DB에서 생성된 ID가 들어있습니다.

        // 2. 방장을 참여 멤버 테이블에 참석으로
        ScheduleMember leader = ScheduleMember.builder()
                .scheduleId(schedule.getId()) // 방금 생성된 ID 사용
                .userId(userId)               // 생성자 ID
                .status(ScheduleMemberStatus.APPLIED)
                .build();

        scheduleMemberMapper.insert(leader);

        log.info("일정 생성 완료 - ID: {}, 생성자: {}", schedule.getId(), userId);
        return schedule.getId();
    }

    @Transactional
    @Override
    public void joinSchedule(Long scheduleId, Long userId) {
        // 1. 일정 존재 여부 확인
        Schedule schedule = scheduleMapper.findById(scheduleId);
        if (schedule == null) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        // 2. 중복 참여 확인
        if (scheduleMemberMapper.countByScheduleIdAndUserId(scheduleId, userId) > 0) {
            throw new CustomException(ErrorCode.ALREADY_JOINED);
        }

        // 3. 정원 초과 확인
        List<ScheduleMember> members = scheduleMemberMapper.findByScheduleId(scheduleId);
        if (members.size() >= schedule.getMaxPeople()) {
            throw new CustomException(ErrorCode.SCHEDULE_FULL);
        }

        // 4. 참여 정보 저장
        ScheduleMember scheduleMember = ScheduleMember.builder()
                .scheduleId(scheduleId)
                .userId(userId)
                .status(ScheduleMemberStatus.APPLIED)
                .build();

        scheduleMemberMapper.insert(scheduleMember);
    }
}
