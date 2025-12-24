package com.ssafy.crewup.schedule.controller;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.schedule.dto.request.ScheduleCreateRequest;
import com.ssafy.crewup.schedule.dto.request.ScheduleMemberStatusUpdateRequest;
import com.ssafy.crewup.schedule.dto.response.ScheduleCreatorCheckResponse;
import com.ssafy.crewup.schedule.dto.response.ScheduleGetResponse;
import com.ssafy.crewup.schedule.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 스케줄 목록 조회
    @GetMapping("/{crewId}/list")
    public ResponseEntity<ApiResponseBody<List<ScheduleGetResponse>>> getScheduleList(
            @PathVariable Long crewId) {

        List<ScheduleGetResponse> schedules = scheduleService.getScheduleList(crewId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_LIST_SUCCESS, schedules)
        );
    }

    // 스케줄 상세 조회
    @GetMapping("/{scheduleId}/detail")
    public ResponseEntity<ApiResponseBody<ScheduleGetResponse>> getScheduleDetail(
            @PathVariable Long scheduleId) {

        ScheduleGetResponse schedule = scheduleService.getScheduleDetail(scheduleId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_DETAIL_SUCCESS, schedule)
        );
    }

    // 일정 생성
    @PostMapping("/{crewId}/create")
    public ResponseEntity<ApiResponseBody<Void>> createSchedule(
            @PathVariable Long crewId,
            @Valid @RequestBody ScheduleCreateRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        scheduleService.createSchedule(request, crewId, userId);

        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_CREATE_SUCCESS));
    }

    // 스케줄 참가
    @PostMapping("/{scheduleId}/join")
    public ResponseEntity<ApiResponseBody<Void>> joinSchedule(
            @PathVariable Long scheduleId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        scheduleService.joinSchedule(scheduleId, userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_JOIN_SUCCESS)
        );
    }
    // 스케줄 삭제
    @DeleteMapping("/{scheduleId}/delete")
    public ResponseEntity<ApiResponseBody<Void>> deleteSchedule(
            @PathVariable Long scheduleId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        scheduleService.deleteSchedule(scheduleId, userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_DELETE_SUCCESS)
        );
    }

    // 생성자 확인
    @GetMapping("/{scheduleId}/status/check")
    public ResponseEntity<ApiResponseBody<ScheduleCreatorCheckResponse>> checkCreator(
            @PathVariable Long scheduleId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        ScheduleCreatorCheckResponse response = scheduleService.checkCreator(scheduleId, userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_CREATOR_CHECK_SUCCESS, response)
        );
    }

    // 멤버 상태 변경
    @PutMapping("/{scheduleId}/status")
    public ResponseEntity<ApiResponseBody<Void>> updateMemberStatus(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleMemberStatusUpdateRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        scheduleService.updateMemberStatus(scheduleId, userId, request);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_MEMBER_STATUS_UPDATE_SUCCESS)
        );
    }
}
