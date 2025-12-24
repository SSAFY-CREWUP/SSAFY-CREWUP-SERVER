package com.ssafy.crewup.board.dto.request;

import com.ssafy.crewup.enums.BoardCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateRequest {
    private BoardCategory category;
    private String title;
    private String content;
}
