package com.ssafy.crewup.user.dto.request;

import com.ssafy.crewup.enums.Gender;
import com.ssafy.crewup.enums.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserAdditionalInfoRequest {

    @NotNull
    private Gender gender;

    @NotNull
    private LocalDate birthDate;

    @NotBlank
    private String averagePace;

    @NotNull
    private Region activityRegion;
}
