package com.ssafy.crewup.course.controller;

import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.request.CourseSearchCondition;
import com.ssafy.crewup.course.dto.request.CourseUpdateRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import com.ssafy.crewup.course.dto.response.CourseReviewResponse;
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
            @ModelAttribute CourseSearchCondition condition // 👈 쿼리 파라미터를 객체로 한 번에 받음
    ) {
        // 만약 radius가 null이면 기본값 설정하는 로직 정도는 서비스에 있으면 좋음
        List<CourseListResponse> courses = courseService.getCourseList(condition);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, courses));
    }

    // 2. 코스 상세 조회
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponseBody<CourseGetResponse>> getCourseDetail(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long userId) {

        CourseGetResponse result = courseService.getCourseDetail(courseId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, result));
    }
    // 3. 코스 등록
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
            log.error(">>> 컨트롤러에서 에러 발생!: ", e);
            throw e;
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

    // ==================== [리뷰 관련] ====================

    // 6. 리뷰 목록 조회
    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<ApiResponseBody<List<CourseReviewResponse>>> getReviewList(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = 1L; // 현재 로그인한 사용자 (내 리뷰 확인용)
        List<CourseReviewResponse> reviews = courseService.getReviewList(courseId, page, size, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_READ_SUCCESS, reviews));
    }

    // 7. 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteReview(@PathVariable Long reviewId) {
        Long userId = 1L;
        courseService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.REVIEW_DELETE_SUCCESS, null));
    }

    // ==================== [스크랩 관련] ====================

    // 8. 내 스크랩 코스 모아보기
    @GetMapping("/scraps")
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getMyScrapCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = 1L;
        List<CourseListResponse> scraps = courseService.getMyScrapCourses(userId, page, size);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_LIST_SUCCESS, scraps));
    }

    // ==================== [내 코스 관리] ====================

    // 9. 내가 만든 코스 조회
    @GetMapping("/my")
    public ResponseEntity<ApiResponseBody<List<CourseListResponse>>> getMyCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = 1L;
        List<CourseListResponse> myCourses = courseService.getMyCourses(userId, page, size);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_READ_SUCCESS, myCourses));
    }

    // 10. 코스 수정
    @PutMapping(value = "/{courseId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponseBody<Void>> updateCourse(
            @PathVariable Long courseId,
            @RequestPart(value = "data") CourseUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Long userId = 1L;
        courseService.updateCourse(courseId, request, image, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_UPDATE_SUCCESS, null));
    }

    // 11. 코스 삭제
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteCourse(@PathVariable Long courseId) {
        Long userId = 1L;
        courseService.deleteCourse(courseId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COURSE_DELETE_SUCCESS, null));
    }

}
