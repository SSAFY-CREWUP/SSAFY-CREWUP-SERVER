package com.ssafy.crewup.course.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WriterDto {
    private Long userId;
    private String nickname;
    private String profileImage;
}
