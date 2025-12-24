package com.ssafy.crewup.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter
@NoArgsConstructor
public class CommentCreateRequest {
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
