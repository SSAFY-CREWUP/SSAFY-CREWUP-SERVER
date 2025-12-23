package com.ssafy.crewup.schedule.mapper;

import com.ssafy.crewup.enums.ScheduleMemberStatus;
import com.ssafy.crewup.schedule.ScheduleMember;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ScheduleMemberMapper {
    @Select("SELECT id, schedule_id AS scheduleId, user_id AS userId, status, attended_at AS attendedAt, created_at AS createdAt, updated_at AS updatedAt FROM schedule_member WHERE id = #{id}")
    ScheduleMember findById(@Param("id") Long id);


    @Update("UPDATE schedule_member SET schedule_id=#{scheduleId}, user_id=#{userId}, status=#{status}, attended_at=#{attendedAt} WHERE id=#{id}")
    int update(ScheduleMember scheduleMember);

    @Delete("DELETE FROM schedule_member WHERE id = #{id}")
    int delete(@Param("id") Long id);


    @Select("SELECT id, schedule_id AS scheduleId, user_id AS userId, status, attended_at AS attendedAt, created_at AS createdAt, updated_at AS updatedAt FROM schedule_member WHERE user_id = #{userId}")
    List<ScheduleMember> findByUserId(@Param("userId") Long userId);

    // 스케줄별 참가자 목록 조회
    @Select("SELECT id, schedule_id AS scheduleId, user_id AS userId, " +
            "status, attended_at AS attendedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM schedule_member " +
            "WHERE schedule_id = #{scheduleId}")
    List<ScheduleMember> findByScheduleId(@Param("scheduleId") Long scheduleId);

    // 사용자가 해당 스케줄에 참가 중인지 확인
    @Select("SELECT COUNT(*) FROM schedule_member " +
            "WHERE schedule_id = #{scheduleId} AND user_id = #{userId}")
    int countByScheduleIdAndUserId(@Param("scheduleId") Long scheduleId,
                                   @Param("userId") Long userId);

    // 스케줄 참가 신청
    @Insert("INSERT INTO schedule_member(schedule_id, user_id, status) " +
            "VALUES(#{scheduleId}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(ScheduleMember scheduleMember);

    // 여러 스케줄의 참가자를 한 번에 조회
    @Select("<script>" +
            "SELECT id, schedule_id AS scheduleId, user_id AS userId, " +
            "status, attended_at AS attendedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM schedule_member " +
            "WHERE schedule_id IN " +
            "<foreach item='id' collection='scheduleIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<ScheduleMember> findByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds);

    @Delete("DELETE FROM schedule_member WHERE schedule_id = #{scheduleId}")
    int deleteByScheduleId(@Param("scheduleId") Long scheduleId);

    // 상태 업데이트
    @Update("UPDATE schedule_member SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") ScheduleMemberStatus status);


}
