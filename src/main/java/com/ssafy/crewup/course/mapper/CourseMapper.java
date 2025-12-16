package com.ssafy.crewup.course.mapper;

import com.ssafy.crewup.course.Course;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CourseMapper {
    @Select("SELECT course_id AS id, writer_id AS writerId, title, description, ST_AsText(path) AS pathWkt, ST_AsText(main_point) AS mainPointWkt, distance, scrap_count AS scrapCount, thumbnail, ai_summary AS aiSummary, JSON_EXTRACT(ai_keywords, '$') AS aiKeywordsJson, created_at AS createdAt, updated_at AS updatedAt FROM course WHERE course_id = #{id}")
    Course findById(@Param("id") Long id);

    @Insert("INSERT INTO course(writer_id, title, description, path, main_point, distance, scrap_count, thumbnail, ai_summary, ai_keywords) VALUES(#{writerId}, #{title}, #{description}, ST_GeomFromText(#{pathWkt}, 4326), ST_GeomFromText(#{mainPointWkt}, 4326), #{distance}, #{scrapCount}, #{thumbnail}, #{aiSummary}, CAST(#{aiKeywordsJson} AS JSON))")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "course_id")
    int insert(Course course);

    @Update("UPDATE course SET title=#{title}, description=#{description}, path=ST_GeomFromText(#{pathWkt}, 4326), main_point=ST_GeomFromText(#{mainPointWkt}, 4326), distance=#{distance}, scrap_count=#{scrapCount}, thumbnail=#{thumbnail}, ai_summary=#{aiSummary}, ai_keywords=CAST(#{aiKeywordsJson} AS JSON) WHERE course_id=#{id}")
    int update(Course course);

    @Delete("DELETE FROM course WHERE course_id = #{id}")
    int delete(@Param("id") Long id);
}
