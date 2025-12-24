package com.ssafy.crewup.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 상대 시간 문자열 생성
     * - 1분 이내: "방금 전"
     * - 59분 이내: "N분 전"
     * - 24시간 이내: "N시간 전"
     * - 3일 이내: "N일 전"
     * - 3일 이후: "yyyy-MM-dd"
     */
    public static String getRelativeTimeString(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분 전";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "시간 전";
        } else if (seconds < 259200) {  // 3일 = 259200초
            return (seconds / 86400) + "일 전";
        } else {
            return createdAt.format(DATE_FORMATTER);
        }
    }
}
