package com.ssafy.crewup.board.mapper;

import com.ssafy.crewup.board.Board;
import com.ssafy.crewup.board.dto.request.BoardSearchCondition;
import com.ssafy.crewup.board.dto.request.BoardUpdateRequest;
import com.ssafy.crewup.board.dto.response.BoardDetailResponse;
import com.ssafy.crewup.board.dto.response.BoardListResponse;
import com.ssafy.crewup.enums.BoardCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    // 1. 게시글 작성
    void insertBoard(Board board);

    // 2. 게시글 상세 조회 (DTO로 바로 반환)
    Optional<BoardDetailResponse> selectBoardDetail(@Param("boardId") Long boardId);

    // 3. 게시글 엔티티 조회 (수정/삭제 권한 확인용)
    Optional<Board> selectBoardById(@Param("boardId") Long boardId);

    // 4. 게시글 목록 조회 (검색 + 페이징)
    List<BoardListResponse> selectBoardList(BoardSearchCondition condition);

    // 5. 메인 홈 미리보기 (카테고리별 최신 N개)
    List<BoardListResponse> selectRecentBoards(@Param("crewId") Long crewId,
                                               @Param("category") BoardCategory category,
                                               @Param("limit") int limit);

    // 6. 게시글 수정
    void updateBoard(@Param("boardId") Long boardId, @Param("request") BoardUpdateRequest request);

    // 7. 게시글 삭제
    void deleteBoard(@Param("boardId") Long boardId);

    // 8. 조회수 증가
    void increaseViewCount(@Param("boardId") Long boardId);
}
