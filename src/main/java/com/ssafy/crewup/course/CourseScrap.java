package com.ssafy.crewup.course;

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
public class CourseScrap extends BaseTime {
    private Long id;        // course_scrap.id
    private Long userId;    // users.user_id
    private Long courseId;  // course.course_id
}
