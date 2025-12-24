package com.ssafy.crewup.board.dto.request;

import com.ssafy.crewup.enums.BoardCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardCreateRequest {
    @NotNull(message = "카테고리는 필수입니다.")
    private BoardCategory category;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
