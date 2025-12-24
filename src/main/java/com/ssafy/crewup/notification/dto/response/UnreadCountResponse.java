package com.ssafy.crewup.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnreadCountResponse {
    private Integer unreadCount;

    public static UnreadCountResponse of(Integer count) {
        return new UnreadCountResponse(count);
    }
}
