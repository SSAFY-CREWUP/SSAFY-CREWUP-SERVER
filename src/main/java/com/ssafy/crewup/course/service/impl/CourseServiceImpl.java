package com.ssafy.crewup.course.service.impl;

import com.ssafy.crewup.course.Course;
import com.ssafy.crewup.course.CourseReview;
import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import com.ssafy.crewup.course.mapper.CourseMapper;
import com.ssafy.crewup.course.service.CourseService;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.global.service.S3Service;
import com.ssafy.crewup.global.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final S3Service S3Service;
    private final GeometryUtil geometryUtil;

    // 1. 코스 등록
    @Transactional
    public Long createCourse(CourseCreateRequest request, MultipartFile image, Long writerId) {
        // 이미지 업로드
        String imageUrl = uploadImage(image, "static/course");

        // 경로 변환 (GeometryUtil 내부에서 괄호 2개인 거 수정했지?)
        String pathWkt = geometryUtil.convertToWkt(request.getPath());
        log.info(">>> 생성된 WKT 문자열: {}", pathWkt);

        // ✅ 수정된 부분: 경도(Lng) 먼저가 아니라, 위도(Lat) 먼저 나오게 수정!
        String mainPointWkt = (request.getPath() != null && !request.getPath().isEmpty())
                ? "POINT(" + request.getPath().get(0).getLat() + " " + request.getPath().get(0).getLng() + ")"
                : null;

        Course course = Course.builder()
                .writerId(writerId)
                .title(request.getTitle())
                .description(request.getDescription())
                .pathWkt(pathWkt)
                .mainPointWkt(mainPointWkt)
                .distance(request.getDistance())
                .expectedTime(request.getExpectedTime())
                .difficulty(request.getDifficulty())
                .thumbnail(imageUrl)
                .scrapCount(0)
                .build();

        courseMapper.insertCourse(course);
        return course.getId();
    }

    // 2. 코스 상세 조회
    @Transactional(readOnly = true)
    public CourseGetResponse getCourseDetail(Long courseId, Long userId) {
        CourseGetResponse response = courseMapper.selectCourseDetail(courseId);
        if (response == null) throw new CustomException(ErrorCode.NOT_FOUND);

        // WKT String -> List<PointDto> 변환
        response.setPath(geometryUtil.convertToPath(response.getPathWkt()));

        // 사용자가 로그인했다면 스크랩 여부 확인
        if (userId != null) {
            response.setIsScrapped(courseMapper.existsScrap(userId, courseId));
        }

        return response;
    }

    // 3. 코스 목록 검색
    public List<CourseListResponse> getCourseList(String keyword, String difficulty) {
        return courseMapper.selectCourseList(keyword, difficulty);
    }

    // 4. 리뷰 등록
    @Transactional
    public void createReview(Long courseId, CourseReviewRequest request, Long writerId) {
        // 필요하다면 이미지 업로드 로직 추가 (request.getImage()가 MultipartFile이라면)
        CourseReview review = CourseReview.builder()
                .courseId(courseId)
                .writerId(writerId)
                .content(request.getContent())
                .rating(request.getRating())
                .image(request.getImage())
                .build();
        courseMapper.insertReview(review);
    }

    @Transactional
    public boolean toggleScrap(Long courseId, Long userId) {
        boolean exists = courseMapper.existsScrap(userId, courseId);

        if (exists) {
            // 이미 스크랩 했으면 -> 취소
            courseMapper.deleteScrap(userId, courseId);
            courseMapper.updateScrapCount(courseId, -1); // 카운트 감소
            return false; // 결과: 스크랩 안 된 상태
        } else {
            // 스크랩 안 했으면 -> 추가
            courseMapper.insertScrap(userId, courseId);
            courseMapper.updateScrapCount(courseId, 1); // 카운트 증가
            return true; // 결과: 스크랩 된 상태
        }
    }

    // 이미지 업로드 헬퍼 메서드
    private String uploadImage(MultipartFile image, String dir) {
        if (image == null || image.isEmpty()) return null;
        return S3Service.uploadFile(image, dir);
    }
}
