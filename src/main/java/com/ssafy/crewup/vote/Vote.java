package com.ssafy.crewup.vote;

import com.ssafy.crewup.global.common.code.BaseTime;
import java.time.LocalDateTime;
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
public class Vote extends BaseTime {
    private Long id;            // vote.vote_id
    private Long crewId;        // crew.crew_id
    private Long creatorId;     // users.user_id
    private String title;
    private LocalDateTime endAt;
    private Boolean multipleChoice;
}
