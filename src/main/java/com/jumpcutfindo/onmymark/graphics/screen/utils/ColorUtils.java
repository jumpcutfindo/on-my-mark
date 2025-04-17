package com.jumpcutfindo.onmymark.graphics.screen.utils;

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
}
