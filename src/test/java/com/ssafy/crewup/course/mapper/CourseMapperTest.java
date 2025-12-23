//package com.ssafy.crewup.course.mapper;
//
//import com.ssafy.crewup.course.Course;
//import com.ssafy.crewup.course.CourseReview;
//import com.ssafy.crewup.course.dto.response.CourseGetResponse;
//import com.ssafy.crewup.course.dto.response.CourseListResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Slf4j
//@MybatisTest // ⭐ 핵심: MyBatis 관련 설정만 로드함 (Controller, Service, S3 로드 안 함)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Transactional  // 테스트 끝나면 DB 롤백 (데이터 안 남음)
//class CourseMapperTest {
//
//    @Autowired
//    private CourseMapper courseMapper;
//
//    @Test
//    @DisplayName("코스 등록 및 상세 조회 테스트 (Geometry 변환 확인)")
//    void createAndSelectCourseTest() {
//        // given
//        Long writerId = 1L; // DB에 존재하는 유저 ID여야 함 (FK 제약조건 주의)
//
//        Course course = Course.builder()
//                .writerId(writerId)
//                .title("마이바티스 테스트 코스")
//                .description("설명")
//
//                // 기존: "LINESTRING(127.1 37.1, 127.2 37.2)" -> 경도(127)가 앞에 와서 에러
//                // 수정: "LINESTRING(37.1 127.1, 37.2 127.2)" -> 위도(37)가 앞에 와야 함!
//                .pathWkt("LINESTRING(37.1 127.1, 37.2 127.2)")
//
//                // 기존: "POINT(127.1 37.1)"
//                // 수정: "POINT(37.1 127.1)"
//                .mainPointWkt("POINT(37.1 127.1)")
//
//                .distance(3000)
//                .expectedTime(20)
//                .difficulty("EASY")
//                .thumbnail("img.jpg")
//                .aiSummary("AI")
//                .aiKeywords("[]")
//                .build();
//
//        // when
//        log.info(">>> 1. 코스 등록 실행");
//        courseMapper.insertCourse(course);
//        Long courseId = course.getId(); // useGeneratedKeys로 ID 채워짐
//
//        log.info(">>> 생성된 Course ID: {}", courseId);
//
//        log.info(">>> 2. 코스 상세 조회 실행");
//        CourseGetResponse result = courseMapper.selectCourseDetail(courseId);
//
//        // then
//        log.info(">>> 3. 결과 검증");
//        assertThat(result).isNotNull();
//        assertThat(result.getCourseId()).isEqualTo(courseId);
//        assertThat(result.getTitle()).isEqualTo("마이바티스 테스트 코스");
//
//        // 가장 중요한 WKT 변환 확인
//        // DB(Geometry) -> MyBatis(ST_AsText) -> Java(String)
//        log.info(">>> 반환된 Path WKT: {}", result.getPathWkt());
//        assertThat(result.getPathWkt()).contains("LINESTRING");
//
//        // JSON 필드 확인
//        // DB(JSON) -> MyBatis(String)
//        // (참고: Mapper XML에서 JSON 타입을 String으로 받을 때 따옴표 이스케이프 이슈가 있을 수 있으니 로그 확인 필수)
//        log.info(">>> 반환된 AI Summary: {}", result.getAiSummary());
//    }
//
//
//    @Test
//    @DisplayName("리뷰 등록 테스트")
//    void insertReviewTest() {
//        // given
//        Course course = createDummyCourse("리뷰 달 코스", "NORMAL");
//        courseMapper.insertCourse(course);
//        Long courseId = course.getId();
//
//        CourseReview review = CourseReview.builder()
//                .courseId(courseId)
//                .writerId(1L) // DB에 있는 유저 ID
//                .content("여기 경치 좋아요!")
//                .rating(5)
//                .image("review.jpg")
//                .build();
//
//        // when
//        courseServiceImpl.insertReview(review);
//
//        // then
//        // 리뷰는 select 메서드가 따로 없어서, 에러 안 나는지로 확인하거나
//        // DB 클라이언트로 확인해야 함. (여기선 에러 안 나면 성공으로 간주)
//        assertThat(review.getId()).isNotNull(); // useGeneratedKeys 작동 확인
//        log.info(">>> 생성된 리뷰 ID: {}", review.getId());
//    }
//
//    @Test
//    @DisplayName("스크랩 추가/삭제 및 조회 테스트")
//    void scrapLifecycleTest() {
//        // given
//        Course course = createDummyCourse("스크랩할 코스", "EASY");
//        courseMapper.insertCourse(course);
//        Long courseId = course.getId();
//        Long userId = 1L;
//
//        // 1. 스크랩 추가
//        courseMapper.insertScrap(userId, courseId);
//        courseMapper.updateScrapCount(courseId, 1); // 카운트 증가
//
//        // 2. 스크랩 존재 여부 확인
//        boolean exists = courseMapper.existsScrap(userId, courseId);
//        assertThat(exists).isTrue();
//
//        // 3. 카운트 증가 확인 (상세 조회로 확인)
//        CourseGetResponse updatedCourse = courseMapper.selectCourseDetail(courseId);
//        assertThat(updatedCourse.getScrapCount()).isEqualTo(1);
//
//        // 4. 스크랩 삭제
//        courseMapper.deleteScrap(userId, courseId);
//        courseMapper.updateScrapCount(courseId, -1); // 카운트 감소
//
//        // 5. 삭제 확인
//        boolean existsAfterDelete = courseMapper.existsScrap(userId, courseId);
//        assertThat(existsAfterDelete).isFalse();
//    }
//
//    // --- 헬퍼 메서드 (테스트용 객체 생성기) ---
//    private Course createDummyCourse(String title, String difficulty) {
//        return Course.builder()
//                .writerId(1L)
//                .title(title)
//                .description("설명")
//                // GeometryUtil 수정했으니 (위도, 경도) 순서!
//                .pathWkt("LINESTRING(37.1 127.1, 37.2 127.2)")
//                .mainPointWkt("POINT(37.1 127.1)")
//                .distance(3000)
//                .expectedTime(20)
//                .difficulty(difficulty)
//                .thumbnail("img.jpg")
//                .aiSummary("AI")
//                .aiKeywords("[]")
//                .build();
//    }
//}
