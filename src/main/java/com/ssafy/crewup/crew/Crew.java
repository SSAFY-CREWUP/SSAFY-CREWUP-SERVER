package com.ssafy.crewup.crew;

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
public class Crew extends BaseTime {
    private Long id;              // crew.crew_id
    private String name;
    private String region;
    private String description;   // TEXT
    private String crewImage;     // TEXT
    private Integer memberCount;
}
