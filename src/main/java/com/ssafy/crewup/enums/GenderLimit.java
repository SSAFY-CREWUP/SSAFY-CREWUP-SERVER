package com.ssafy.crewup.enums;

public enum GenderLimit {
    모두,
    남성,
    여성;

    public static boolean isValid(String value) {
        for (GenderLimit g : values()) {
            if (g.name().equals(value)) return true;
        }
        return false;
    }
}
