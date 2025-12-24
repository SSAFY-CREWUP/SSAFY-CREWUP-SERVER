package com.ssafy.crewup.crew.mapper;

import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.dto.response.CrewMemberDetailResponse;

import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CrewMemberMapper {
    @Select("SELECT id, crew_id AS crewId, user_id AS userId, role, status, applied_at AS appliedAt, joined_at AS joinedAt, updated_at AS updatedAt, created_at AS createdAt FROM crew_member WHERE id = #{id}")
    CrewMember findById(@Param("id") Long id);

    @Insert("INSERT INTO crew_member(crew_id, user_id, role, status, applied_at, joined_at) VALUES(#{crewId}, #{userId}, #{role}, #{status}, #{appliedAt}, #{joinedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(CrewMember crewMember);

    @Update("UPDATE crew_member SET crew_id=#{crewId}, user_id=#{userId}, role=#{role}, status=#{status}, applied_at=#{appliedAt}, joined_at=#{joinedAt} WHERE id=#{id}")
    int update(CrewMember crewMember);

    @Delete("DELETE FROM crew_member WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT id, crew_id AS crewId, user_id AS userId, role, status, applied_at AS appliedAt, joined_at AS joinedAt, updated_at AS updatedAt FROM crew_member WHERE crew_id = #{crewId}")
    List<CrewMember> findByCrewId(@Param("crewId") Long crewId);

    @Select("SELECT id, crew_id AS crewId, user_id AS userId, role, status, applied_at AS appliedAt, joined_at AS joinedAt, updated_at AS updatedAt, created_at AS createdAt FROM crew_member WHERE user_id = #{userId}")
    List<CrewMember> findByUserId(@Param("userId") Long userId);

	@Select("SELECT id, crew_id AS crewId, user_id AS userId, role, status, " +
		"applied_at AS appliedAt, joined_at AS joinedAt " +
		"FROM crew_member WHERE crew_id = #{crewId} AND user_id = #{userId}")
	CrewMember findByCrewIdAndUserId(@Param("crewId") Long crewId, @Param("userId") Long userId);

    /**
     * 크루의 승인된 멤버 조회 (ACCEPTED 상태만)
     */
    @Select("SELECT id, crew_id AS crewId, user_id AS userId, " +
            "role, status, applied_at AS appliedAt, joined_at AS joinedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM crew_member " +
            "WHERE crew_id = #{crewId} AND status = 'ACCEPTED' " +
            "ORDER BY joined_at ASC")
    List<CrewMember> findAcceptedMembersByCrewId(@Param("crewId") Long crewId);

    /**
     * 크루 멤버 ID 리스트 조회 (알림 발송용)
     */
    @Select("SELECT user_id FROM crew_member " +
            "WHERE crew_id = #{crewId} AND status = 'ACCEPTED'")
    List<Long> findMemberIdsByCrewId(@Param("crewId") Long crewId);

    /**
     * 크루의 가입 대기 중인 멤버 조회 (WAITING 상태만)
     */
    @Select("SELECT id, crew_id AS crewId, user_id AS userId, " +
            "role, status, applied_at AS appliedAt, joined_at AS joinedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM crew_member " +
            "WHERE crew_id = #{crewId} AND status = 'WAITING' " +
            "ORDER BY applied_at ASC")
    List<CrewMember> findWaitingMembersByCrewId(@Param("crewId") Long crewId);


}

