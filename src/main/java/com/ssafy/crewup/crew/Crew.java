package com.ssafy.crewup.crew;

import com.ssafy.crewup.global.common.code.BaseTime;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Crew extends BaseTime {
	private Long id;
	private String name;
	private String region;
	private String description;
	private String crewImage;
	private Integer memberCount;
	private String activityTime;
	private String ageGroup;
	private String genderLimit;
	private Double averagePace;
	private List<String> keywords;
}