package com.jumpcutfindo.onmymark.graphics.screen.utils;

import java.awt.*;

public class ScreenUtils {
    public static boolean isWithin(double mouseX, double mouseY, int boundX, int boundY, int boundWidth, int boundHeight) {
        return mouseX >= boundX && mouseX < boundX + boundWidth
                && mouseY >= boundY && mouseY < boundY + boundHeight;
    }

    public static int getColorOfIndex(int index) {
        if (index == -1) {
            return 0xFFFFFFFF;
        }

        // Convert index into a hue value between 0.0 and 1.0
        float hue = (index % 360) / 360.0f;

        // Saturation and brightness are full (1.0f)
        Color color = Color.getHSBColor(hue, 1.0f, 1.0f);

        // Compose ARGB (fully opaque)
        int alpha = 255;
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
