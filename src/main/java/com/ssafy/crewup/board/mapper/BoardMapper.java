package com.ssafy.crewup.board.mapper;

import com.ssafy.crewup.board.Board;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BoardMapper {
    @Select("SELECT board_id AS id, crew_id AS crewId, writer_id AS writerId, category, title, content, view_count AS viewCount, created_at AS createdAt, updated_at AS updatedAt FROM board WHERE board_id = #{id}")
    Board findById(@Param("id") Long id);

    @Insert("INSERT INTO board(crew_id, writer_id, category, title, content, view_count) VALUES(#{crewId}, #{writerId}, #{category}, #{title}, #{content}, #{viewCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "board_id")
    int insert(Board board);

    @Update("UPDATE board SET crew_id=#{crewId}, writer_id=#{writerId}, category=#{category}, title=#{title}, content=#{content}, view_count=#{viewCount} WHERE board_id=#{id}")
    int update(Board board);

    @Delete("DELETE FROM board WHERE board_id = #{id}")
    int delete(@Param("id") Long id);
}
