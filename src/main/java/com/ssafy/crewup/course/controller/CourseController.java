package com.ssafy.crewup.course.controller;

import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.request.CourseSearchCondition;
import com.ssafy.crewup.course.dto.request.CourseUpdateRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import com.ssafy.crewup.course.dto.response.CourseReviewResponse;
import com.ssafy.crewup.course.service.CourseService;
import com.ssafy.crewup.global.annotation.LoginUser;
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

    // ==================== [API Endpoints] ====================

    // 1. 코스 목록 검색 (로그인 필수)
    @GetMapping
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getCourseList(
            @ModelAttribute CourseSearchCondition condition,
            @LoginUser Long userId
    ) {
        condition.setUserId(userId);
        List<CourseListResponse> courses = courseService.getCourseList(condition);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, courses));
    }

    // 2. 코스 상세 조회 (로그인 필수)
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponseBody<CourseGetResponse>> getCourseDetail(
            @PathVariable Long courseId,
            @LoginUser Long userId) {

        CourseGetResponse result = courseService.getCourseDetail(courseId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, result));
    }

    // 3. 코스 등록 (로그인 필수)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody<Long>> createCourse(
            @RequestPart("data") CourseCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @LoginUser Long userId) {

        Long courseId = courseService.createCourse(request, image, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBody.onSuccess(SuccessCode.COURSE_CREATE_SUCCESS, courseId));
    }

    // 4. 리뷰 등록 (로그인 필수)
    @PostMapping(value = "/{courseId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody<Void>> createReview(
            @PathVariable Long courseId,
            @RequestPart("data") CourseReviewRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @LoginUser Long userId) {
        log.info("--- Controller 진입 ---");
        log.info("--- User 검증 종료 ---");
        courseService.createReview(courseId, image, request, userId);

        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_CREATE_SUCCESS, null));
    }

    // 5. 스크랩 (토글) (로그인 필수)
    @PostMapping("/{courseId}/scrap")
    public ResponseEntity<ApiResponseBody<Boolean>> toggleScrap(
            @PathVariable Long courseId,
            @LoginUser Long userId) {

        boolean result = courseService.toggleScrap(courseId, userId);

        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_SCRAP_SUCCESS, result));
    }

    // ==================== [리뷰 관련] ====================

    // 6. 리뷰 목록 조회 (로그인 필수)
    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseBody<List<CourseReviewResponse>>> getReviewList(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @LoginUser Long userId
    ) {
        List<CourseReviewResponse> reviews = courseService.getReviewList(courseId, page, size, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_READ_SUCCESS, reviews));
    }

    // 7. 리뷰 삭제 (로그인 필수)
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteReview(
            @PathVariable Long reviewId,
            @LoginUser Long userId) {

        courseService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_DELETE_SUCCESS, null));
    }

    // ==================== [스크랩 관련] ====================

    // 8. 내 스크랩 코스 모아보기 (로그인 필수)
    @GetMapping("/scraps")
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getMyScrapCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @LoginUser Long userId
    ) {
        List<CourseListResponse> scraps = courseService.getMyScrapCourses(userId, page, size);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_LIST_SUCCESS, scraps));
    }

    // ==================== [내 코스 관리] ====================

    // 9. 내가 만든 코스 조회 (로그인 필수)
    @GetMapping("/my")
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getMyCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @LoginUser Long userId
    ) {
        List<CourseListResponse> myCourses = courseService.getMyCourses(userId, page, size);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, myCourses));
    }

    // 10. 코스 수정 (로그인 필수)
    @PutMapping(value = "/{courseId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseBody<Void>> updateCourse(
            @PathVariable Long courseId,
            @RequestPart(value = "data") CourseUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @LoginUser Long userId
    ) {
        courseService.updateCourse(courseId, request, image, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_UPDATE_SUCCESS, null));
    }

    // 11. 코스 삭제 (로그인 필수)
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteCourse(
            @PathVariable Long courseId,
            @LoginUser Long userId) {

        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_DELETE_SUCCESS, null));
    }
}
