package com.ssafy.crewup.course.controller;

import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import com.ssafy.crewup.course.service.CourseService;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    // 1. 코스 목록 검색
    @GetMapping
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getCourseList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String difficulty) {

        List<CourseListResponse> result = courseService.getCourseList(keyword, difficulty);
        // ResponseEntity로 감싸고, onSuccess 호출
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_LIST_SUCCESS, result));
    }

    // 2. 코스 상세 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponseBody<CourseGetResponse>> getCourseDetail(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long userId) {

        CourseGetResponse result = courseService.getCourseDetail(courseId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, result));
    }
    // 3. 코스 등록 (디버깅용 try-catch 추가)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody<Long>> createCourse(
            @RequestPart("data") CourseCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        log.info(">>> 코스 등록 요청 들어옴! 제목: {}", request.getTitle()); // 1. 요청 도착 확인

        try {
            Long userId = 1L;
            Long courseId = courseService.createCourse(request, image, userId);

            log.info(">>> 서비스 로직 성공! ID: {}", courseId); // 2. 성공 확인

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseBody.onSuccess(SuccessCode.COURSE_CREATE_SUCCESS, courseId));

        } catch (Exception e) {
            // 🔥 여기가 핵심! 에러가 나면 여기서 무조건 찍힘
            log.error(">>> 🚨 컨트롤러에서 에러 포착!!: ", e);
            throw e; // 로그 찍고 다시 던져서 원래 흐름대로 가게 둠
        }
    }

    // 4. 리뷰 등록
    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseBody<Void>> createReview(
            @PathVariable Long courseId,
            @RequestBody CourseReviewRequest request) {

        Long userId = 1L; // 임시 하드코딩
        courseService.createReview(courseId, request, userId);

        // 데이터가 없으므로 두 번째 인자는 null 혹은 생략 (구현에 따라 다름)
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_CREATE_SUCCESS, null));
    }

    // 5. 스크랩 (토글)
    @PostMapping("/{courseId}/scrap")
    public ResponseEntity<ApiResponseBody<Boolean>> toggleScrap(@PathVariable Long courseId) {

        Long userId = 1L; // 임시 하드코딩
        boolean result = courseService.toggleScrap(courseId, userId);

        // result(true/false)를 데이터로 반환
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_SCRAP_SUCCESS, result));
    }

}
