package com.ssafy.crewup.board.service;

import com.ssafy.crewup.board.dto.request.*;
import com.ssafy.crewup.board.dto.response.*;

import java.util.List;

public interface BoardService {

    // [게시글]
    Long createBoard(Long crewId, BoardCreateRequest request, Long userId);
    BoardDetailResponse getBoardDetail(Long crewId, Long boardId, Long userId);
    List<BoardListResponse> getBoardList(BoardSearchCondition condition);
    BoardHomeResponse getHomeBoards(Long crewId);
    void updateBoard(Long boardId, BoardUpdateRequest request, Long userId);
    void deleteBoard(Long boardId, Long userId);

    // [댓글]
    void createComment(Long boardId, CommentCreateRequest request, Long userId);
    List<CommentResponse> getCommentList(Long boardId, int page, int size, Long userId);
    void updateComment(Long commentId, CommentCreateRequest request, Long userId);
    void deleteComment(Long commentId, Long userId);
}
