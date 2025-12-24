package com.ssafy.crewup.vote.mapper;

import java.util.List;

import com.ssafy.crewup.vote.Vote;
import org.apache.ibatis.annotations.*;

@Mapper
public interface VoteMapper {
	@Insert("INSERT INTO vote(crew_id, creator_id, title, end_at, multiple_choice, is_anonymous, limit_count) " +
		"VALUES(#{crewId}, #{creatorId}, #{title}, #{endAt}, #{multipleChoice}, #{isAnonymous}, #{limitCount})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "vote_id") // keyColumn 추가
	int insert(Vote vote);

	@Select("SELECT vote_id AS id, crew_id AS crewId, creator_id AS creatorId, title, end_at AS endAt, " +
		"multiple_choice AS multipleChoice, is_anonymous AS isAnonymous, limit_count AS limitCount " +
		"FROM vote WHERE vote_id = #{id}")
	Vote findById(Long id);

	@Select("""
				SELECT
					v.vote_id AS voteId,
					v.title,
					v.end_at AS endAt,
					false AS isClosed,
					v.limit_count AS limitCount,
					(SELECT COUNT(DISTINCT user_id) FROM vote_record WHERE vote_id = v.vote_id) AS participantCount,
					v.multiple_choice AS multipleChoice,
					v.is_anonymous AS isAnonymous
				FROM vote v
				WHERE v.crew_id = #{crewId} AND v.end_at > NOW()
				ORDER BY v.created_at DESC
			""")
	List<com.ssafy.crewup.vote.dto.response.VoteSummary> findActiveVotes(Long crewId);

	@Select("""
				SELECT
					v.vote_id AS voteId,
					v.title,
					v.end_at AS endAt,
					true AS isClosed,
					v.limit_count AS limitCount,
					(SELECT COUNT(DISTINCT user_id) FROM vote_record WHERE vote_id = v.vote_id) AS participantCount,
					v.multiple_choice AS multipleChoice,
					v.is_anonymous AS isAnonymous
				FROM vote v
				WHERE v.crew_id = #{crewId} AND v.end_at <= NOW()
				ORDER BY v.end_at DESC
			""")
	List<com.ssafy.crewup.vote.dto.response.VoteSummary> findEndedVotes(Long crewId);

	@Update("UPDATE vote SET end_at = NOW() WHERE vote_id = #{id}")
	int closeVote(Long id);

	@Delete("DELETE FROM vote WHERE vote_id = #{id}")
	int delete(Long id);
}
