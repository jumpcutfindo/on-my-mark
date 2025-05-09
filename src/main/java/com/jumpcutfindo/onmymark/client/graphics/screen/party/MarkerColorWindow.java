package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class MarkerColorWindow extends OnMyMarkWindow {
    public static final int WINDOW_WIDTH = 138, WINDOW_HEIGHT = 92;

    public MarkerColorWindow(OnMyMarkScreen screen) {
        super(screen, Text.literal("Select a color"), WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return List.of();
    }
}
