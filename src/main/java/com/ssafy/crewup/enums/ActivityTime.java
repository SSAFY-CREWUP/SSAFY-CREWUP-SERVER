package com.ssafy.crewup.enums;

public enum ActivityTime {
    오전,
    점심,
    저녁,
    야간;

    public static boolean isValid(String value) {
        for (ActivityTime t : values()) {
            if (t.name().equals(value)) return true;
        }
        return false;
    }
}
