package com.ssafy.crewup.vote.mapper;

import com.ssafy.crewup.vote.Vote;
import org.apache.ibatis.annotations.*;

@Mapper
public interface VoteMapper {
    @Select("SELECT vote_id AS id, crew_id AS crewId, creator_id AS creatorId, title, end_at AS endAt, multiple_choice AS multipleChoice, created_at AS createdAt, updated_at AS updatedAt FROM vote WHERE vote_id = #{id}")
    Vote findById(@Param("id") Long id);

    @Insert("INSERT INTO vote(crew_id, creator_id, title, end_at, multiple_choice) VALUES(#{crewId}, #{creatorId}, #{title}, #{endAt}, #{multipleChoice})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "vote_id")
    int insert(Vote vote);

    @Update("UPDATE vote SET crew_id=#{crewId}, creator_id=#{creatorId}, title=#{title}, end_at=#{endAt}, multiple_choice=#{multipleChoice} WHERE vote_id=#{id}")
    int update(Vote vote);

    @Delete("DELETE FROM vote WHERE vote_id = #{id}")
    int delete(@Param("id") Long id);
}
