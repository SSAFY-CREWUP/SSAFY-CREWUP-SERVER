package com.ssafy.crewup.schedule.service;

import com.ssafy.crewup.schedule.dto.request.ScheduleCreateRequest;
import com.ssafy.crewup.schedule.dto.response.ScheduleGetResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScheduleService {
    // 스케줄 목록 조회
    List<ScheduleGetResponse> getScheduleList(Long crewId);

    // 스케줄 상세 조회
    ScheduleGetResponse getScheduleDetail(Long scheduleId);

    // 스케줄 생성
    Long createSchedule(ScheduleCreateRequest request, Long userId);

    void joinSchedule(Long scheduleId, Long userId);
}
