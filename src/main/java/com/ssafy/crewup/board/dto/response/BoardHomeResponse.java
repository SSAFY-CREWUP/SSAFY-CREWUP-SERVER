package com.ssafy.crewup.board.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardHomeResponse {
    private List<BoardListResponse> noticeList; // 최신 공지 3개
    private List<BoardListResponse> freeList;   // 최신 자유 3개
}
