package com.ssafy.crewup.board.service.impl;

import com.ssafy.crewup.board.Board;
import com.ssafy.crewup.board.Comment;
import com.ssafy.crewup.board.dto.request.*;
import com.ssafy.crewup.board.dto.response.*;
import com.ssafy.crewup.board.mapper.BoardMapper;
import com.ssafy.crewup.board.mapper.CommentMapper;
import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.enums.BoardCategory;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.board.service.BoardService;
import com.ssafy.crewup.enums.CrewMemberStatus;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;
    private final CrewMemberMapper crewMemberMapper;

    // [게시글] 작성
    @Override
    @Transactional
    public Long createBoard(Long crewId, BoardCreateRequest request, Long userId) {
        // 크루 멤버 검증
        CrewMember member = validateCrewMember(crewId, userId);

        // 공지사항 권한 체크 (LEADER, MANAGER만 가능)
        if (request.getCategory() == BoardCategory.NOTICE) {
            // 위에서 가져온 member 객체로 바로 권한 확인
            if (member.getRole() == CrewMemberRole.MEMBER) {
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        }

        // 엔티티 생성 및 저장
        Board board = Board.builder()
                .crewId(crewId)
                .writerId(userId)
                .category(request.getCategory())
                .title(request.getTitle())
                .content(request.getContent())
                .viewCount(0)
                .build();

        boardMapper.insertBoard(board);
        return board.getId();
    }

    // [게시글] 상세 조회
    @Override
    @Transactional
    public BoardDetailResponse getBoardDetail(Long crewId, Long boardId, Long userId) {
        // 멤버 검증
        validateCrewMember(crewId, userId);

        // 조회수 증가
        boardMapper.increaseViewCount(boardId);

        // 상세 정보 조회
        BoardDetailResponse response = boardMapper.selectBoardDetail(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND)); // ErrorCode 확인

        // 본인 글 여부 체크 (수정/삭제 버튼용)
        response.setIsWriter(response.getWriterId().equals(userId));

        return response;
    }

    // [게시글] 목록 조회 (검색 + 페이징)
    @Override
    public List<BoardListResponse> getBoardList(BoardSearchCondition condition) {
        // Offset 계산
        int offset = Math.max(0, condition.getPage()) * condition.getSize();
        condition.setOffset(offset);

        return boardMapper.selectBoardList(condition);
    }

    // [게시글] 메인 홈 미리보기 (공지 3개 + 자유 3개)
    @Override
    public BoardHomeResponse getHomeBoards(Long crewId) {
        List<BoardListResponse> notices = boardMapper.selectRecentBoards(crewId, BoardCategory.NOTICE, 3);
        List<BoardListResponse> frees = boardMapper.selectRecentBoards(crewId, BoardCategory.FREE, 3);

        return BoardHomeResponse.builder()
                .noticeList(notices)
                .freeList(frees)
                .build();
    }

    // [게시글] 수정
    @Override
    @Transactional
    public void updateBoard(Long boardId, BoardUpdateRequest request, Long userId) {
        Board board = boardMapper.selectBoardById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        boardMapper.updateBoard(boardId, request);
    }

    // [게시글] 삭제
    @Override
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardMapper.selectBoardById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        boardMapper.deleteBoard(boardId);
    }

    // [댓글] 작성
    @Override
    @Transactional
    public void createComment(Long boardId, CommentCreateRequest request, Long userId) {
        Comment comment = Comment.builder()
                .boardId(boardId)
                .writerId(userId)
                .content(request.getContent())
                .build();

        commentMapper.insertComment(comment);
    }

    // [댓글] 목록 조회
    @Override
    public List<CommentResponse> getCommentList(Long boardId, int page, int size, Long userId) {
        int offset = page * size;
        List<CommentResponse> comments = commentMapper.selectCommentList(boardId, offset, size);

        // 본인 댓글 여부 마킹
        comments.forEach(c -> c.setIsWriter(c.getWriterId().equals(userId)));

        return comments;
    }

    // [댓글] 수정
    @Override
    @Transactional
    public void updateComment(Long commentId, CommentCreateRequest request, Long userId) {
        Comment comment = commentMapper.selectCommentById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)); // ErrorCode 확인

        if (!comment.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        commentMapper.updateComment(commentId, request.getContent());
    }

    // [댓글] 삭제
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectCommentById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        commentMapper.deleteComment(commentId);
    }

    // ==================== [Private Helpers] ====================
    // 크루 멤버 검증 메서드
    private CrewMember validateCrewMember(Long crewId, Long userId) {
        CrewMember member = crewMemberMapper.findByCrewIdAndUserId(crewId, userId);
        // 가입하지 않았거나 승인 대기중이면 접근 승인 X
        if (member == null || member.getStatus() != CrewMemberStatus.ACCEPTED) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    return member;
    }
}
