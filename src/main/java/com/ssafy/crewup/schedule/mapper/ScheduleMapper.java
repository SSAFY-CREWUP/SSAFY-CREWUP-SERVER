package com.ssafy.crewup.schedule.mapper;

import com.ssafy.crewup.schedule.Schedule;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ScheduleMapper {
    @Select("SELECT schedule_id AS id, crew_id AS crewId, course_id AS courseId, title, run_date AS runDate, location, max_people AS maxPeople, created_at AS createdAt, updated_at AS updatedAt FROM schedule WHERE schedule_id = #{id}")
    Schedule findById(@Param("id") Long id);

    @Insert("INSERT INTO schedule(crew_id, course_id, title, run_date, location, max_people) VALUES(#{crewId}, #{courseId}, #{title}, #{runDate}, #{location}, #{maxPeople})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "schedule_id")
    int insert(Schedule schedule);

    @Update("UPDATE schedule SET crew_id=#{crewId}, course_id=#{courseId}, title=#{title}, run_date=#{runDate}, location=#{location}, max_people=#{maxPeople} WHERE schedule_id=#{id}")
    int update(Schedule schedule);

    @Delete("DELETE FROM schedule WHERE schedule_id = #{id}")
    int delete(@Param("id") Long id);
}
