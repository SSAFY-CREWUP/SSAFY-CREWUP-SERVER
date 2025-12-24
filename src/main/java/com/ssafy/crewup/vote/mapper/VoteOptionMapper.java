package com.ssafy.crewup.vote.mapper;

import com.ssafy.crewup.vote.VoteOption;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VoteOptionMapper {
	@Select("SELECT option_id AS id, vote_id AS voteId, content, count, created_at AS createdAt, updated_at AS updatedAt FROM vote_option WHERE option_id = #{id}")
	VoteOption findById(@Param("id") Long id);

	@Insert("INSERT INTO vote_option(vote_id, content, count) VALUES(#{voteId}, #{content}, #{count})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "option_id")
	int insert(VoteOption option);

	@Update("UPDATE vote_option SET content=#{content}, count=#{count} WHERE option_id=#{id}")
	int update(VoteOption option);

	@Delete("DELETE FROM vote_option WHERE option_id = #{id}")
	int delete(@Param("id") Long id);

	@Select("SELECT option_id AS id, vote_id AS voteId, content, count, created_at AS createdAt, updated_at AS updatedAt FROM vote_option WHERE vote_id = #{voteId}")
	List<VoteOption> findByVoteId(@Param("voteId") Long voteId);

	@Select("""
                <script>
                SELECT option_id AS id, vote_id AS voteId, content, count, created_at AS createdAt, updated_at AS updatedAt
                FROM vote_option
                WHERE vote_id IN
                <foreach item='id' collection='voteIds' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
                </script>
            """)
	List<VoteOption> findByVoteIdIn(@Param("voteIds") List<Long> voteIds);

	@Select("SELECT * FROM vote_option WHERE option_id = #{id} FOR UPDATE")
	VoteOption findByIdWithLock(Long id);

	@Update("UPDATE vote_option SET count = count + 1 WHERE option_id = #{id}")
	void incrementCount(Long id);

}
