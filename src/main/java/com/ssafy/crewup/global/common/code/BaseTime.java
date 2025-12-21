package com.ssafy.crewup.global.common.code;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseTime {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
