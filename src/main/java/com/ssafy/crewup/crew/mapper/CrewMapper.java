package com.ssafy.crewup.crew.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.dto.response.CrewMemberDetailResponse;
import com.ssafy.crewup.crew.dto.request.CrewSearchRequest;
import com.ssafy.crewup.crew.dto.response.CrewListResponse;

@Mapper
public interface CrewMapper {

	@Insert("INSERT INTO crew(name, region, description, crew_image, member_count, activity_time, age_group, gender_limit, average_pace, keywords) "
		+
		"VALUES(#{name}, #{region}, #{description}, #{crewImage}, #{memberCount}, #{activityTime}, #{ageGroup}, #{genderLimit}, #{averagePace}, "
		+
		"#{keywords, typeHandler=com.ssafy.crewup.global.config.StringListTypeHandler})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "crew_id")
	int insert(Crew crew);

	@Select("SELECT crew_id AS id, name, region, description, crew_image AS crewImage, " +
		"member_count AS memberCount, activity_time AS activityTime, age_group AS ageGroup, " +
		"gender_limit AS genderLimit, average_pace AS averagePace, keywords, " +
		"created_at AS createdAt, updated_at AS updatedAt FROM crew WHERE crew_id = #{id}")
	@Results(id = "CrewResultMap", value = {
		@Result(property = "keywords", column = "keywords", typeHandler = com.ssafy.crewup.global.config.StringListTypeHandler.class)
	})
	Crew findById(@Param("id") Long id);

	List<CrewListResponse> searchCrews(CrewSearchRequest request);

	@Select("SELECT u.user_id AS userId, u.nickname, u.profile_image AS profileImage, cm.role " +
		"FROM crew_member cm " +
		"JOIN users u ON cm.user_id = u.user_id " +
		"WHERE cm.crew_id = #{crewId} AND cm.status = 'ACCEPTED'")
	List<CrewMemberDetailResponse> findMembersByCrewId(@Param("crewId") Long crewId);

	List<CrewListResponse> findCrewsByUserId(@Param("userId") Long userId);


    // ⭐ 추가: 크루 정보 업데이트
    @Update("UPDATE crew SET " +
            "name = #{name}, " +
            "region = #{region}, " +
            "description = #{description}, " +
            "crew_image = #{crewImage}, " +
            "member_count = #{memberCount}, " +
            "activity_time = #{activityTime}, " +
            "age_group = #{ageGroup}, " +
            "gender_limit = #{genderLimit}, " +
            "average_pace = #{averagePace}, " +
            "keywords = #{keywords, typeHandler=com.ssafy.crewup.global.config.StringListTypeHandler}, " +
            "updated_at = NOW() " +
            "WHERE crew_id = #{id}")
    int update(Crew crew);
}
