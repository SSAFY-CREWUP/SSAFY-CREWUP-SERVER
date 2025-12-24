package com.ssafy.crewup.schedule.mapper;

import com.ssafy.crewup.schedule.Schedule;
import org.apache.ibatis.annotations.*;
import java.util.List;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    // 스케줄 목록 조회
    @Select("SELECT schedule_id AS id, crew_id AS crewId, course_id AS courseId, " +
            "title, run_date AS runDate, location, max_people AS maxPeople, " +
            "content, schedule_type AS scheduleType, " +
            "created_at AS createdAt " +  // updated_at 제거
            "FROM schedule " +
            "WHERE crew_id = #{crewId} " +
            "ORDER BY run_date DESC")
    List<Schedule> findByCrewId(@Param("crewId") Long crewId);

    // 스케줄 상세 조회
    @Select("SELECT schedule_id AS id, crew_id AS crewId, course_id AS courseId, " +
            "title, run_date AS runDate, location, max_people AS maxPeople, " +
            "content, schedule_type AS scheduleType, " +
            "created_at AS createdAt " +  // updated_at 제거
            "FROM schedule " +
            "WHERE schedule_id = #{scheduleId}")
    Schedule findById(@Param("scheduleId") Long scheduleId);

    @Insert("INSERT INTO schedule(crew_id, course_id, title, run_date, location, max_people, content, schedule_type) " +
            "VALUES(#{crewId}, #{courseId}, #{title}, #{runDate}, #{location}, #{maxPeople}, #{content}, #{scheduleType})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "schedule_id")
    int insert(Schedule schedule);

    @Update("UPDATE schedule SET crew_id=#{crewId}, course_id=#{courseId}, title=#{title}, run_date=#{runDate}, location=#{location}, max_people=#{maxPeople} WHERE schedule_id=#{id}")
    int update(Schedule schedule);

    @Delete("DELETE FROM schedule WHERE schedule_id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 특정 시간 범위 내에 시작하는 일정 조회
     * - 스케줄러에서 사용
     */
    @Select("SELECT schedule_id AS id, crew_id AS crewId, course_id AS courseId, " +
            "title, run_date AS runDate, location, max_people AS maxPeople, " +
            "content, schedule_type AS scheduleType, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM schedules " +
            "WHERE run_date BETWEEN #{startRange} AND #{endRange}")
    List<Schedule> findByRunDateBetween(
            @Param("startRange") LocalDateTime startRange,
            @Param("endRange") LocalDateTime endRange
    );
}
