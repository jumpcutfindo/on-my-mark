package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.ColorSlider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Predicate;

public class MarkerColorWindow extends OnMyMarkWindow {
    public static final int WINDOW_WIDTH = 138, WINDOW_HEIGHT = 92;
    public static final int SLIDER_WIDTH = 128, SLIDER_HEIGHT = 18;

    private static final Predicate<String> COLOR_VALUE_PREDICATE = (text) -> {
        try {
            int value = Integer.parseInt(text);
            return value >= 0 && value <= 255;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    private final ColorSlider redSlider, greenSlider, blueSlider;
    private final TextFieldWidget redField, greenField, blueField;

    public MarkerColorWindow(OnMyMarkScreen screen) {
        super(screen, Text.literal("Select a color"), WINDOW_WIDTH, WINDOW_HEIGHT);

        this.redSlider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, Text.literal("Red"), 0);
        this.redField = new TextFieldWidget(screen.getTextRenderer(), 36, SLIDER_HEIGHT, Text.literal(Integer.toString(redSlider.getColorValue())));
        this.redField.setTextPredicate(COLOR_VALUE_PREDICATE);

        this.greenSlider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, Text.literal("Green"), 0);
        this.greenField = new TextFieldWidget(screen.getTextRenderer(), 36, SLIDER_HEIGHT, Text.literal(Integer.toString(greenSlider.getColorValue())));
        this.redField.setTextPredicate(COLOR_VALUE_PREDICATE);

        this.blueSlider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, Text.literal("Blue"), 0);
        this.blueField = new TextFieldWidget(screen.getTextRenderer(), 36, SLIDER_HEIGHT, Text.literal(Integer.toString(blueSlider.getColorValue())));
        this.blueField.setTextPredicate(COLOR_VALUE_PREDICATE);
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
        blueField.render(context, mouseX, mouseY, 0);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(redSlider, redField, greenSlider, greenField, blueSlider, blueField);
    }
}
