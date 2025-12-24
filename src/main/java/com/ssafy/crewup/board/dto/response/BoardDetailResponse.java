package com.ssafy.crewup.board.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.crewup.enums.BoardCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardDetailResponse {
    private Long boardId;
    private Long crewId;
    private BoardCategory category;
    private String title;
    private String content;
    private int viewCount;

    // 작성자 정보
    private Long writerId;
    private String writerNickname;
    private String writerProfileImage;

    // 권한 확인용 (프론트에서 수정/삭제 버튼 보여줄지 결정)
    private Boolean isWriter;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
