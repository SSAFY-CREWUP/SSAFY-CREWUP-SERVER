package com.ssafy.crewup.schedule.dto.response;

import com.ssafy.crewup.enums.ScheduleType;
import com.ssafy.crewup.schedule.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleGetResponse {
    private Long scheduleId;
    private Long crewId;
    private Long courseId;
    private String title;
    private LocalDateTime runDate;
    private String location;
    private Integer maxPeople;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ScheduleType scheduleType;

    // 참가자 정보
    private Integer currentPeople;  // 현재 참가 인원 목록 리스트의 크기
    private List<ScheduleMemberResponse> members;  // 참가자 목록

//    public static ScheduleGetResponse from(Schedule schedule) {
//        return ScheduleGetResponse.builder()
//                .scheduleId(schedule.getId())
//                .crewId(schedule.getCrewId())
//                .courseId(schedule.getCourseId())
//                .title(schedule.getTitle())
//                .runDate(schedule.getRunDate())
//                .location(schedule.getLocation())
//                .maxPeople(schedule.getMaxPeople())
//                .content(schedule.getContent())
//                .scheduleType(schedule.getScheduleType())
//                .createdAt(schedule.getCreatedAt())
//                .updatedAt(schedule.getUpdatedAt())
//                .build();
//    }

    public static ScheduleGetResponse from(Schedule schedule, List<ScheduleMemberResponse> members) {
        return ScheduleGetResponse.builder()
                .scheduleId(schedule.getId())
                .crewId(schedule.getCrewId())
                .courseId(schedule.getCourseId())
                .title(schedule.getTitle())
                .runDate(schedule.getRunDate())
                .location(schedule.getLocation())
                .maxPeople(schedule.getMaxPeople())
                .content(schedule.getContent())
                .scheduleType(schedule.getScheduleType())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .currentPeople(members.size())
                .members(members)
                .build();
    }
}
