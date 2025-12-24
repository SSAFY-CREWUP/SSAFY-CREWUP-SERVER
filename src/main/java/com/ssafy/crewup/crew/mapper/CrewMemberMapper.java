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
}
