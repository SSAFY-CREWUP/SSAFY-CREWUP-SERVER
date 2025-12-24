package com.ssafy.crewup.board.dto.request;

import com.ssafy.crewup.enums.BoardCategory;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardSearchCondition {
    private Long crewId;
    private String keyword;     // 제목 검색 (선택)
    private BoardCategory category; // 카테고리 필터 (선택)

    private int page = 0;
    private int size = 10;
    private int offset;
}
