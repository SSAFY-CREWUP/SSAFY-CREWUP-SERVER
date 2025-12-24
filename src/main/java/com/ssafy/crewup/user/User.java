package com.ssafy.crewup.user;

import com.ssafy.crewup.enums.Gender;
import com.ssafy.crewup.enums.Region;
import com.ssafy.crewup.global.common.code.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTime {
    private Long id;              // users.user_id
    private String email;
    private String password;
    private String nickname;
    private String profileImage;  // TEXT
    private Integer totalDistance;

    // 추가 정보
    private Gender gender;
    private LocalDate birthDate;
    private String averagePace;
    private Region activityRegion;
}
