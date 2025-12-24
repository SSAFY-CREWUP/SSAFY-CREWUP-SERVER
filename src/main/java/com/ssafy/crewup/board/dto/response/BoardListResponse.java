package com.ssafy.crewup.board.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.crewup.enums.BoardCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardListResponse {
    private Long boardId;
    private BoardCategory category;
    private String title;
    private int viewCount;
    private int commentCount; // 댓글 수 (서브쿼리로 가져옴)

    // 작성자 정보
    private Long writerId;
    private String writerNickname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
