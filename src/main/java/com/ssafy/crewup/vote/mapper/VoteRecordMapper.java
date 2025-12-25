package com.ssafy.crewup.vote.mapper;

import com.ssafy.crewup.vote.VoteRecord;
import com.ssafy.crewup.vote.dto.response.VoteResultResponse;

import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VoteRecordMapper {
	@Select("SELECT id, vote_option_id AS voteOptionId, user_id AS userId, vote_id AS voteId, created_at AS createdAt, updated_at AS updatedAt FROM vote_record WHERE id = #{id}")
	VoteRecord findById(@Param("id") Long id);

	@Insert("INSERT INTO vote_record(vote_option_id, user_id, vote_id) VALUES(#{voteOptionId}, #{userId}, #{voteId})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	int insert(VoteRecord record);

	@Delete("DELETE FROM vote_record WHERE id = #{id}")
	int delete(@Param("id") Long id);

	@Select("SELECT id, vote_option_id AS voteOptionId, user_id AS userId, vote_id AS voteId, created_at AS createdAt, updated_at AS updatedAt FROM vote_record WHERE vote_id = #{voteId}")
	List<VoteRecord> findByVoteId(@Param("voteId") Long voteId);

	@Select("SELECT id, vote_option_id AS voteOptionId, user_id AS userId, vote_id AS voteId, created_at AS createdAt, updated_at AS updatedAt FROM vote_record WHERE user_id = #{userId}")
	List<VoteRecord> findByUserId(@Param("userId") Long userId);

	@Select("""
				SELECT u.nickname, u.profile_image as profileImage, r.created_at as votedAt
				FROM vote_record r
				JOIN users u ON r.user_id = u.user_id
				WHERE r.vote_option_id = #{optionId}
			""")
	List<VoteResultResponse.VoterInfo> findVotersByOptionId(@Param("optionId") Long optionId);

	@Select("SELECT COUNT(*) > 0 FROM vote_record WHERE user_id = #{userId} AND vote_id = #{voteId}")
	boolean existsByUserIdAndVoteId(@Param("userId") Long userId, @Param("voteId") Long voteId);
}
