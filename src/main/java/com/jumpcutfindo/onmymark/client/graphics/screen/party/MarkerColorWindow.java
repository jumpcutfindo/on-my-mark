package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.ColorSlider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.List;

public class MarkerColorWindow extends OnMyMarkWindow {
    public static final int WINDOW_WIDTH = 138, WINDOW_HEIGHT = 92;

    private final SliderWidget redSlider, greenSlider, blueSlider;

    public MarkerColorWindow(OnMyMarkScreen screen) {
        super(screen, Text.literal("Select a color"), WINDOW_WIDTH, WINDOW_HEIGHT);

        this.redSlider = new ColorSlider(128, 16, Text.literal("Red"), 0);
        this.greenSlider = new ColorSlider(128, 16, Text.literal("Green"), 0);
        this.blueSlider = new ColorSlider(128, 16, Text.literal("Blue"), 0);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        super.renderContent(context, mouseX, mouseY);

        redSlider.setPosition(x, y + 16);
        redSlider.render(context, mouseX, mouseY, 0);

        greenSlider.setPosition(x, y + 32);
        greenSlider.render(context, mouseX, mouseY, 0);

        blueSlider.setPosition(x, y + 48);
        blueSlider.render(context, mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return redSlider.isMouseOver(mouseX, mouseY) && redSlider.mouseClicked(mouseX, mouseY, button)
                || greenSlider.isMouseOver(mouseX, mouseY) && greenSlider.mouseClicked(mouseX, mouseY, button)
                || blueSlider.isMouseOver(mouseX, mouseY) && blueSlider.mouseClicked(mouseX, mouseY, button)
                || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return redSlider.isMouseOver(mouseX, mouseY) && redSlider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                || greenSlider.isMouseOver(mouseX, mouseY) && greenSlider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                || blueSlider.isMouseOver(mouseX, mouseY) && blueSlider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return redSlider.isMouseOver(mouseX, mouseY) && redSlider.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
                || greenSlider.isMouseOver(mouseX, mouseY) && greenSlider.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
                || blueSlider.isMouseOver(mouseX, mouseY) && blueSlider.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
                || super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return List.of(redSlider, greenSlider, blueSlider);
    }
}
