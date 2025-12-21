package com.ssafy.crewup.vote;

import com.ssafy.crewup.global.common.code.BaseTime;
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
public class VoteOption extends BaseTime {
    private Long id;         // vote_option.option_id
    private Long voteId;     // vote.vote_id
    private String content;
    private Integer count;
}
