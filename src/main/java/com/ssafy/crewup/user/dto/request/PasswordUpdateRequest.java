package com.ssafy.crewup.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordUpdateRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}