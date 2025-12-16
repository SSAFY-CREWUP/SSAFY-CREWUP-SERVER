package com.ssafy.crewup.vote;

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
public class VoteRecord extends BaseTime {
    private Long id;            // vote_record.id
    private Long voteOptionId;  // vote_option.option_id
    private Long userId;        // users.user_id
    private Long voteId;        // vote.vote_id
}
