package com.ssafy.crewup.schedule.mapper;

import com.ssafy.crewup.schedule.ScheduleMember;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ScheduleMemberMapper {
    @Select("SELECT id, schedule_id AS scheduleId, user_id AS userId, status, attended_at AS attendedAt, created_at AS createdAt, updated_at AS updatedAt FROM schedule_member WHERE id = #{id}")
    ScheduleMember findById(@Param("id") Long id);

    @Insert("INSERT INTO schedule_member(schedule_id, user_id, status, attended_at) VALUES(#{scheduleId}, #{userId}, #{status}, #{attendedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(ScheduleMember scheduleMember);

    @Update("UPDATE schedule_member SET schedule_id=#{scheduleId}, user_id=#{userId}, status=#{status}, attended_at=#{attendedAt} WHERE id=#{id}")
    int update(ScheduleMember scheduleMember);

    @Delete("DELETE FROM schedule_member WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT id, schedule_id AS scheduleId, user_id AS userId, status, attended_at AS attendedAt, created_at AS createdAt, updated_at AS updatedAt FROM schedule_member WHERE schedule_id = #{scheduleId}")
    List<ScheduleMember> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Select("SELECT id, schedule_id AS scheduleId, user_id AS userId, status, attended_at AS attendedAt, created_at AS createdAt, updated_at AS updatedAt FROM schedule_member WHERE user_id = #{userId}")
    List<ScheduleMember> findByUserId(@Param("userId") Long userId);
}
