package com.ssafy.crewup.board.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class CommentResponse {
    private Long commentId;
    private String content;

    // 작성자 정보
    private Long writerId;
    private String writerNickname;
    private String writerProfileImage;

    // 본인 댓글 여부
    private Boolean isWriter;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
