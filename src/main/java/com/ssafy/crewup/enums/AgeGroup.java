package com.ssafy.crewup.enums;

public enum AgeGroup {
    전연령,
    _1020,
    _2030,
    _3040,
    _4050,
    _5060;

    public static boolean isValid(String value) {
        // allow both with and without underscore style
        if ("1020".equals(value)) return true;
        if ("2030".equals(value)) return true;
        if ("3040".equals(value)) return true;
        if ("4050".equals(value)) return true;
        if ("5060".equals(value)) return true;
        for (AgeGroup g : values()) {
            if (g.name().equals(value)) return true;
        }
        return false;
    }
}
