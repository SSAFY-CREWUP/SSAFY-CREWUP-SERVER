package com.ssafy.crewup.board;

import com.ssafy.crewup.global.common.BaseTime;
import com.ssafy.crewup.enums.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends BaseTime {
    private Long id;                // board.board_id
    private Long crewId;            // crew.crew_id
    private Long writerId;          // users.user_id
    private BoardCategory category; // ENUM
    private String title;
    private String content;         // TEXT
    private Integer viewCount;
}
