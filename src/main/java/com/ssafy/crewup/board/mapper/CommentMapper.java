package com.ssafy.crewup.board.mapper;

import com.ssafy.crewup.board.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("SELECT comment_id AS id, board_id AS boardId, writer_id AS writerId, content, created_at AS createdAt, updated_at AS updatedAt FROM comment WHERE comment_id = #{id}")
    Comment findById(@Param("id") Long id);

    @Insert("INSERT INTO comment(board_id, writer_id, content) VALUES(#{boardId}, #{writerId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "comment_id")
    int insert(Comment comment);

    @Update("UPDATE comment SET content=#{content} WHERE comment_id=#{id}")
    int update(Comment comment);

    @Delete("DELETE FROM comment WHERE comment_id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT comment_id AS id, board_id AS boardId, writer_id AS writerId, content, created_at AS createdAt, updated_at AS updatedAt FROM comment WHERE board_id = #{boardId} ORDER BY comment_id ASC")
    List<Comment> findByBoardId(@Param("boardId") Long boardId);
}
