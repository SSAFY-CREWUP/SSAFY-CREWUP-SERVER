package com.ssafy.crewup.user.dto.request;

import com.ssafy.crewup.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;

    private String profileImage;

    // DTO -> Entity 변환
    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .profileImage(this.profileImage)
                .totalDistance(0)
                .build();
    }
}

