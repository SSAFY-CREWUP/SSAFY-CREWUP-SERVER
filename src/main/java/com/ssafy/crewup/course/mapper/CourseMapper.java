package com.ssafy.crewup.course.mapper;

import com.ssafy.crewup.course.Course;
import com.ssafy.crewup.course.CourseReview;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {
//    @Select("SELECT course_id AS id, writer_id AS writerId, title, description, ST_AsText(path) AS pathWkt, ST_AsText(main_point) AS mainPointWkt, distance, scrap_count AS scrapCount, thumbnail, ai_summary AS aiSummary, JSON_EXTRACT(ai_keywords, '$') AS aiKeywordsJson, created_at AS createdAt, updated_at AS updatedAt FROM course WHERE course_id = #{id}")
//    Course findById(@Param("id") Long id);
//
//    @Insert("INSERT INTO course(writer_id, title, description, path, main_point, distance, scrap_count, thumbnail, ai_summary, ai_keywords) VALUES(#{writerId}, #{title}, #{description}, ST_GeomFromText(#{pathWkt}, 4326), ST_GeomFromText(#{mainPointWkt}, 4326), #{distance}, #{scrapCount}, #{thumbnail}, #{aiSummary}, CAST(#{aiKeywordsJson} AS JSON))")
//    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "course_id")
//    int insert(Course course);
//
//    @Update("UPDATE course SET title=#{title}, description=#{description}, path=ST_GeomFromText(#{pathWkt}, 4326), main_point=ST_GeomFromText(#{mainPointWkt}, 4326), distance=#{distance}, scrap_count=#{scrapCount}, thumbnail=#{thumbnail}, ai_summary=#{aiSummary}, ai_keywords=CAST(#{aiKeywordsJson} AS JSON) WHERE course_id=#{id}")
//    int update(Course course);
//
//    @Delete("DELETE FROM course WHERE course_id = #{id}")
//    int delete(@Param("id") Long id);

    // 1. 코스 등록 (XML id="insertCourse")
    // useGeneratedKeys 덕분에 실행 후 course 객체의 id 필드에 값이 채워짐
    void insertCourse(Course course);

    // 2. 코스 상세 조회 (XML id="selectCourseDetail")
    CourseGetResponse selectCourseDetail(@Param("courseId") Long courseId);

    // 3. 코스 목록 검색 (XML id="selectCourseList")
    // 파라미터가 2개 이상이거나 동적 쿼리에 쓸 변수명은 @Param 붙이는 게 안전해
    List<CourseListResponse> selectCourseList(@Param("keyword") String keyword,
                                              @Param("difficulty") String difficulty);

    // 4. 리뷰 등록 (XML id="insertReview")
    void insertReview(CourseReview review);

    // 5. 스크랩 관련 (XML id="existsScrap", "insertScrap", "deleteScrap", "updateScrapCount")
    boolean existsScrap(@Param("userId") Long userId, @Param("courseId") Long courseId);

    void insertScrap(@Param("userId") Long userId, @Param("courseId") Long courseId);

    void deleteScrap(@Param("userId") Long userId, @Param("courseId") Long courseId);

    void updateScrapCount(@Param("courseId") Long courseId, @Param("delta") int delta);
}
