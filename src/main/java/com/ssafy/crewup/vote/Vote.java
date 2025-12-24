package com.ssafy.crewup.vote;

import com.ssafy.crewup.global.common.code.BaseTime;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vote extends BaseTime {
	private Long id;
	private Long crewId;
	private Long creatorId;
	private String title;
	private LocalDateTime endAt;
	private Boolean multipleChoice;
	private Boolean isAnonymous;
	private Integer limitCount;
}
