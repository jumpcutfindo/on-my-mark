package com.jumpcutfindo.onmymark.client.graphics.screen.components;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OnMyMarkButton extends ButtonWidget {
    public OnMyMarkButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }
}
