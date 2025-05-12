package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.ColorSlider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class MarkerColorWindow extends OnMyMarkWindow {
    public static final int WINDOW_WIDTH = 138, WINDOW_HEIGHT = 92;
    public static final int SLIDER_WIDTH = 128, SLIDER_HEIGHT = 18;

    private final ColorSlider redSlider, greenSlider, blueSlider;
    private final TextFieldWidget redField, greenField, blueField;

    public MarkerColorWindow(OnMyMarkScreen screen) {
        super(screen, Text.literal("Select a color"), WINDOW_WIDTH, WINDOW_HEIGHT);

        this.redSlider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, Text.literal("Red"), 0);
        this.redField = new TextFieldWidget(screen.getTextRenderer(), 64, SLIDER_HEIGHT, Text.literal(Integer.toString(redSlider.getColorValue())));

        this.greenSlider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, Text.literal("Green"), 0);
        this.greenField = new TextFieldWidget(screen.getTextRenderer(), 64, SLIDER_HEIGHT, Text.literal(Integer.toString(greenSlider.getColorValue())));

        this.blueSlider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, Text.literal("Blue"), 0);
        this.blueField = new TextFieldWidget(screen.getTextRenderer(), 64, SLIDER_HEIGHT, Text.literal(Integer.toString(blueSlider.getColorValue())));
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        super.renderContent(context, mouseX, mouseY);

        redSlider.setPosition(x, y + 16);
        redSlider.render(context, mouseX, mouseY, 0);
        redField.setPosition(x + SLIDER_WIDTH, y + 16);
        redField.render(context, mouseX, mouseY, 0);

        greenSlider.setPosition(x, y + 32);
        greenSlider.render(context, mouseX, mouseY, 0);
        greenField.setPosition(x + SLIDER_WIDTH, y + 32);
        greenField.render(context, mouseX, mouseY, 0);

        blueSlider.setPosition(x, y + 48);
        blueSlider.render(context, mouseX, mouseY, 0);
        blueField.setPosition(x + SLIDER_WIDTH, y + 48);
        redField.render(context, mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        Optional<ClickableWidget> widget = getHoveredWidget(this.getWidgets(), mouseX, mouseY);

        if (widget.isPresent()) {
            return widget.get().mouseClicked(mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        Optional<ClickableWidget> widget = getHoveredWidget(this.getWidgets(), mouseX, mouseY);

        if (widget.isPresent()) {
            return widget.get().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Optional<ClickableWidget> widget = getHoveredWidget(this.getWidgets(), mouseX, mouseY);

        if (widget.isPresent()) {
            return widget.get().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private static Optional<ClickableWidget> getHoveredWidget(List<ClickableWidget> widgets, double mouseX, double mouseY) {
        return widgets.stream()
                .filter(cw -> cw.isMouseOver(mouseX, mouseY))
                .findFirst();
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return List.of(redSlider, redField, greenSlider, greenField, blueSlider, blueField);
    }
}
