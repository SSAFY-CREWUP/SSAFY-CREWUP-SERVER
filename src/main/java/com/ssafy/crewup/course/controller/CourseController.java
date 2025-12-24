package com.ssafy.crewup.course.controller;

import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.request.CourseSearchCondition;
import com.ssafy.crewup.course.dto.request.CourseUpdateRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import com.ssafy.crewup.course.dto.response.CourseReviewResponse;
import com.ssafy.crewup.course.service.CourseService;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import jakarta.servlet.http.HttpSession;
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

    // ==================== [Helper Method] ====================

    /**
     * 세션에서 userId를 추출합니다.
     * 세션이 없거나 userId가 없으면 무조건 401 에러를 던집니다. (전체 서비스 필수)
     */
    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return userId;
    }

    // ==================== [API Endpoints] ====================

    // 1. 코스 목록 검색 (로그인 필수)
    @GetMapping
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getCourseList(
            @ModelAttribute CourseSearchCondition condition,
            HttpSession session
    ) {
        log.info(condition.toString());

        Long userId = getUserIdOrThrow(session);

        condition.setUserId(userId);

        List<CourseListResponse> courses = courseService.getCourseList(condition);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, courses));
    }

    // 2. 코스 상세 조회 (로그인 필수)
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponseBody<CourseGetResponse>> getCourseDetail(
            @PathVariable Long courseId,
            HttpSession session) {

        Long userId = getUserIdOrThrow(session); // 이제 무조건 유저 ID가 있음
        CourseGetResponse result = courseService.getCourseDetail(courseId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, result));
    }

    // 3. 코스 등록 (로그인 필수)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody<Long>> createCourse(
            @RequestPart("data") CourseCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpSession session) {

        Long userId = getUserIdOrThrow(session);

        Long courseId = courseService.createCourse(request, image, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBody.onSuccess(SuccessCode.COURSE_CREATE_SUCCESS, courseId));
    }

    // 4. 리뷰 등록 (로그인 필수)
    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseBody<Void>> createReview(
            @PathVariable Long courseId,
            @RequestBody CourseReviewRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpSession session) {

        Long userId = getUserIdOrThrow(session);
        courseService.createReview(courseId, image, request, userId);

        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_CREATE_SUCCESS, null));
    }

    // 5. 스크랩 (토글) (로그인 필수)
    @PostMapping("/{courseId}/scrap")
    public ResponseEntity<ApiResponseBody<Boolean>> toggleScrap(
            @PathVariable Long courseId,
            HttpSession session) {

        Long userId = getUserIdOrThrow(session);
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
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session); // 내 리뷰인지(isWriter) 판단하기 위해 필요
        List<CourseReviewResponse> reviews = courseService.getReviewList(courseId, page, size, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_READ_SUCCESS, reviews));
    }

    // 7. 리뷰 삭제 (로그인 필수)
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteReview(
            @PathVariable Long reviewId,
            HttpSession session) {

        Long userId = getUserIdOrThrow(session);
        courseService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_DELETE_SUCCESS, null));
    }

    // ==================== [스크랩 관련] ====================

    // 8. 내 스크랩 코스 모아보기 (로그인 필수)
    @GetMapping("/scraps")
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getMyScrapCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        List<CourseListResponse> scraps = courseService.getMyScrapCourses(userId, page, size);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_LIST_SUCCESS, scraps));
    }

    // ==================== [내 코스 관리] ====================

    // 9. 내가 만든 코스 조회 (로그인 필수)
    @GetMapping("/my")
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getMyCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        List<CourseListResponse> myCourses = courseService.getMyCourses(userId, page, size);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, myCourses));
    }

    // 10. 코스 수정 (로그인 필수)
    @PutMapping(value = "/{courseId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseBody<Void>> updateCourse(
            @PathVariable Long courseId,
            @RequestPart(value = "data") CourseUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        courseService.updateCourse(courseId, request, image, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_UPDATE_SUCCESS, null));
    }

    // 11. 코스 삭제 (로그인 필수)
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteCourse(
            @PathVariable Long courseId,
            HttpSession session) {

        Long userId = getUserIdOrThrow(session);
        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_DELETE_SUCCESS, null));
    }
}
