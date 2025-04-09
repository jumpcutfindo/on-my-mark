package com.jumpcutfindo.onmymark.graphics.screen.utils;

public class StringUtils {
    public static String truncatedName(String name, int maxLength) {
        if (name.length() <= maxLength) return name;
        else return name.substring(0, maxLength) + "...";
    }
}
