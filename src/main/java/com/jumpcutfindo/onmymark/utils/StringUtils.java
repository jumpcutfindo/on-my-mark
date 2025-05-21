package com.jumpcutfindo.onmymark.utils;

public class StringUtils {
    public static String truncatedName(String name, int maxLength) {
        if (name.length() <= maxLength) return name;
        else return name.substring(0, maxLength) + "...";
    }

    public static String intToHexColor(int value) {
        int rgb = value & 0xFFFFFF;
        return String.format("#%06X", rgb);
    }
}
