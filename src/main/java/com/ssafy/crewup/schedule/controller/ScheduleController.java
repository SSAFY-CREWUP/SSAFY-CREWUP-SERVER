package com.ssafy.crewup.schedule.controller;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.schedule.dto.request.ScheduleCreateRequest;
import com.ssafy.crewup.schedule.dto.response.ScheduleGetResponse;
import com.ssafy.crewup.schedule.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 스케줄 목록 조회
    @GetMapping("/list/{crewId}")
    public ResponseEntity<ApiResponseBody<List<ScheduleGetResponse>>> getScheduleList(
            @PathVariable Long crewId) {

        List<ScheduleGetResponse> schedules = scheduleService.getScheduleList(crewId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_LIST_SUCCESS, schedules)
        );
    }

    // 스케줄 상세 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ApiResponseBody<ScheduleGetResponse>> getScheduleDetail(
            @PathVariable Long scheduleId) {

        ScheduleGetResponse schedule = scheduleService.getScheduleDetail(scheduleId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_DETAIL_SUCCESS, schedule)
        );
    }
//    // ScheduleController.java
//    @PostMapping("/create")
//    public ResponseEntity<ApiResponseBody<Void>> createSchedule(
//            @Valid @RequestBody ScheduleCreateRequest request) {
//
//        log.info("=== [Controller] createSchedule 진입 ==="); // 이게 찍히는지 확인!
//        log.info("Request Body: {}", request);
//
//        scheduleService.createSchedule(request);
//        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_CREATE_SUCCESS));
//    }

//    // ScheduleController.java
//    @PostMapping("/{crewId}/create") // URL에 crewId 포함
//    public ResponseEntity<ApiResponseBody<Void>> createSchedule(
//            @PathVariable Long crewId, // 경로 파라미터로 받음
//            @Valid @RequestBody ScheduleCreateRequest request) {
//
//        // DTO 내부의 crewId를 경로 파라미터 값으로 강제 설정 (보안 및 정합성)
//        request.setCrewId(crewId);
//
//        scheduleService.createSchedule(request);
//
//        return ResponseEntity.ok(
//                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_CREATE_SUCCESS)
//        );
//    }
// ScheduleController.java
@PostMapping("/{crewId}/create")
public ResponseEntity<ApiResponseBody<Void>> createSchedule(
        @PathVariable Long crewId,
        @Valid @RequestBody ScheduleCreateRequest request,
        HttpSession session) {

    Long userId = (Long) session.getAttribute("userId"); // 세션에서 유저 ID 추출

    request.setCrewId(crewId);
    scheduleService.createSchedule(request, userId); // userId 함께 전달

    return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_CREATE_SUCCESS));
}

    // 스케줄 참가
    // ScheduleController.java

    @PostMapping("/{scheduleId}/join/{userId}")
    public ResponseEntity<ApiResponseBody<Void>> joinSchedule(
            @PathVariable Long scheduleId,
            @PathVariable Long userId,
            HttpSession session) {

        // 1. 보안 체크: 세션의 유저와 경로의 userId가 일치하는지 검증
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // 본인이 아니면 요청 거부
        }

        // 2. 서비스 호출
        scheduleService.joinSchedule(scheduleId, userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.SCHEDULE_JOIN_SUCCESS)
        );
    }
}
