	package com.ssafy.crewup.vote.mapper;

	import java.util.List;

	import com.ssafy.crewup.vote.Vote;
	import com.ssafy.crewup.vote.dto.response.VoteResponse;

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
				vote_id AS voteId, 
				title, 
				end_at AS endAt, 
				false AS isClosed 
			FROM vote 
			WHERE crew_id = #{crewId} AND end_at > NOW() 
			ORDER BY created_at DESC
		""")
		List<VoteResponse> findActiveVotes(Long crewId);

		@Select("""
			SELECT 
				vote_id AS voteId, 
				title, 
				end_at AS endAt, 
				true AS isClosed 
			FROM vote 
			WHERE crew_id = #{crewId} AND end_at <= NOW() 
			ORDER BY end_at DESC
		""")
		List<VoteResponse> findEndedVotes(Long crewId);

		@Update("UPDATE vote SET end_at = NOW() WHERE vote_id = #{id}")
		int closeVote(Long id);

		@Delete("DELETE FROM vote WHERE vote_id = #{id}")
		int delete(Long id);
	}
