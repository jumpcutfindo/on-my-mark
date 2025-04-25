package com.jumpcutfindo.onmymark.client.graphics.screen.utils;

import java.awt.*;

public class ColorUtils {
    /**
     * Sets the opacity of a color.
     *
     * @param color The original ARGB color (e.g., 0xFF0000FF).
     * @param alpha A float between 0.0 (fully transparent) and 1.0 (fully opaque).
     * @return The color with modified alpha.
     */
    public static int setOpacity(int color, float alpha) {
        // Clamp alpha to [0, 1]
        alpha = Math.max(0f, Math.min(1f, alpha));

        // Extract RGB components
        int rgb = color & 0x00FFFFFF;

        // Compute new alpha value
        int newAlpha = (int) (alpha * 255) << 24;

        // Combine new alpha with original RGB
        return newAlpha | rgb;
    }

    // TODO: Fix this formula, or implement better way for color
    public static int getColorOfIndex(int index) {
        if (index == -1) {
            return 0xFFFFFFFF;
        }

        // Convert index into a hue value between 0.0 and 1.0
        float hue = (index % 360) / 360.0f * 80.0f;

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
