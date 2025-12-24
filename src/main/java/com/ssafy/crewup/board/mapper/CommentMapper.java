package com.ssafy.crewup.board.mapper;

import com.ssafy.crewup.board.Comment;
import com.ssafy.crewup.board.dto.response.CommentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {
    // 1. 댓글 작성
    void insertComment(Comment comment);

    // 2. 댓글 목록 조회
    List<CommentResponse> selectCommentList(@Param("boardId") Long boardId,
                                            @Param("offset") int offset,
                                            @Param("size") int size);

    // 3. 댓글 엔티티 조회 (수정/삭제 권한 확인용)
    Optional<Comment> selectCommentById(@Param("commentId") Long commentId);

    // 4. 댓글 수정
    void updateComment(@Param("commentId") Long commentId, @Param("content") String content);

    // 5. 댓글 삭제
    void deleteComment(@Param("commentId") Long commentId);
}
