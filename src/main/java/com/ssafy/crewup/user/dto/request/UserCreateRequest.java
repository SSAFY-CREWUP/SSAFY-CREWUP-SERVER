package com.ssafy.crewup.user.dto.request;

import com.ssafy.crewup.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    public User toEntity(String password, String profileImageUrl) {
        return User.builder()
                .email(this.email)
                .password(password)
                .nickname(this.nickname)
                .profileImage(profileImageUrl)
                .totalDistance(0)
                .build();
    }
}
