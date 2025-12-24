package com.ssafy.crewup.board.controller;

import com.ssafy.crewup.board.dto.request.*;
import com.ssafy.crewup.board.dto.response.*;
import com.ssafy.crewup.board.service.BoardService;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/crew/{crewId}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 헬퍼 메서드 (생략 - 기존과 동일)
    private Long getUserIdOrThrow(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        return userId;
    }

    // ==================== [게시글 API] ====================

    // 1. 메인 홈 미리보기
    // GET /api/v1/crew/{crewId}/boards/home
    @GetMapping("/home")
    public ResponseEntity<ApiResponseBody<BoardHomeResponse>> getHomeBoards(
            @PathVariable Long crewId,
            HttpSession session
    ) {
        getUserIdOrThrow(session);
        BoardHomeResponse response = boardService.getHomeBoards(crewId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.BOARD_READ_SUCCESS, response));
    }

    // 2. 게시글 목록 조회
    // GET /api/v1/crew/{crewId}/boards
    @GetMapping
    public ResponseEntity<ApiResponseBody<List<BoardListResponse>>> getBoardList(
            @PathVariable Long crewId,
            @ModelAttribute BoardSearchCondition condition,
            HttpSession session
    ) {
        getUserIdOrThrow(session);

        condition.setCrewId(crewId);

        List<BoardListResponse> response = boardService.getBoardList(condition);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.BOARD_READ_SUCCESS, response));
    }

    // 3. 게시글 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponseBody<BoardDetailResponse>> getBoardDetail(
            @PathVariable Long crewId,
            @PathVariable Long boardId,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        BoardDetailResponse response = boardService.getBoardDetail(crewId, boardId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.BOARD_READ_SUCCESS, response));
    }

    // 4. 게시글 작성
    // POST /api/v1/crew/{crewId}/boards
    @PostMapping
    public ResponseEntity<ApiResponseBody<Long>> createBoard(
            @PathVariable Long crewId,
            @Valid @RequestBody BoardCreateRequest request,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        Long boardId = boardService.createBoard(crewId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBody.onSuccess(SuccessCode.BOARD_CREATE_SUCCESS, boardId));
    }

    // 5. 게시글 수정
    // PUT /api/v1/crew/{crewId}/boards/{boardId}
    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponseBody<Void>> updateBoard(
            @PathVariable Long crewId,
            @PathVariable Long boardId,
            @RequestBody BoardUpdateRequest request,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        boardService.updateBoard(boardId, request, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.BOARD_UPDATE_SUCCESS, null));
    }

    // 6. 게시글 삭제
    // DELETE /api/v1/crew/{crewId}/boards/{boardId}
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteBoard(
            @PathVariable Long crewId,
            @PathVariable Long boardId,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.BOARD_DELETE_SUCCESS, null));
    }

    // ==================== [댓글 API] ====================

    // 7. 댓글 목록 조회
    // GET /api/v1/crew/{crewId}/boards/{boardId}/comments
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<ApiResponseBody<List<CommentResponse>>> getCommentList(
            @PathVariable Long crewId,
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        List<CommentResponse> response = boardService.getCommentList(boardId, page, size, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COMMENT_READ_SUCCESS, response));
    }

    // 8. 댓글 작성
    // POST /api/v1/crew/{crewId}/boards/{boardId}/comments
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<ApiResponseBody<Void>> createComment(
            @PathVariable Long crewId,
            @PathVariable Long boardId,
            @Valid @RequestBody CommentCreateRequest request,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        boardService.createComment(boardId, request, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COMMENT_CREATE_SUCCESS, null));
    }

    // 9. 댓글 수정
    // PUT /api/v1/crew/{crewId}/boards/comments/{commentId}
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseBody<Void>> updateComment(
            @PathVariable Long crewId,
            @PathVariable Long commentId,
            @RequestBody CommentCreateRequest request,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        boardService.updateComment(commentId, request, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COMMENT_UPDATE_SUCCESS, null));
    }

    // 10. 댓글 삭제
    // DELETE /api/v1/crew/{crewId}/boards/comments/{commentId}
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteComment(
            @PathVariable Long crewId,
            @PathVariable Long commentId,
            HttpSession session
    ) {
        Long userId = getUserIdOrThrow(session);
        boardService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.COMMENT_DELETE_SUCCESS, null));
    }
}
