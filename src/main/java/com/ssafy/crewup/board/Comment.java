package com.ssafy.crewup.board;

import com.ssafy.crewup.global.common.BaseTime;
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
public class Comment extends BaseTime {
    private Long id;         // comment.comment_id
    private Long boardId;    // board.board_id
    private Long writerId;   // users.user_id
    private String content;  // TEXT
}
