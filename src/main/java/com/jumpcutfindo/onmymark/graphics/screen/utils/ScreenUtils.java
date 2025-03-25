package com.jumpcutfindo.onmymark.graphics.screen.utils;

public class ScreenUtils {
    public static boolean isWithin(double mouseX, double mouseY, int boundX, int boundY, int boundWidth, int boundHeight) {
        return mouseX >= boundX && mouseX < boundX + boundWidth
                && mouseY >= boundY && mouseY < boundY + boundHeight;
    }
}
