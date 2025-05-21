package com.jumpcutfindo.onmymark.utils;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class StringUtils {
    public static String truncatedName(String name, int maxLength) {
        if (name.length() <= maxLength) return name;
        else return name.substring(0, maxLength) + "...";
    }

    public static String intToHexColor(int value) {
        int rgb = value & 0xFFFFFF;
        return String.format("#%06X", rgb);
    }

    public static Text coordinatesAsFancyText(double x, double y, double z, int color) {
        return Texts.bracketed(Text.translatable("chat.coordinates", (int) x, (int) y, (int) z)).styled((style) -> {
            Style textStyled = style.withColor(Formatting.GREEN);
            return textStyled
                    .withClickEvent(new ClickEvent.SuggestCommand(String.format("/tp @s %d %d %d", (int) x, (int) y, (int) z)))
                    .withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.coordinates.tooltip")))
                    .withColor(color);
        });
    }
}
