package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.ColorSlider;
import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ColorUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MarkerColorWindow extends OnMyMarkWindow {
    public static final int WINDOW_WIDTH = 138, WINDOW_HEIGHT = 92;

    private final int initialColor;

    private final ColorSliderWithField redWidget, greenWidget, blueWidget;

    public MarkerColorWindow(OnMyMarkScreen screen, int initialColor) {
        super(screen, Text.literal("Select a color"), WINDOW_WIDTH, WINDOW_HEIGHT);

        this.initialColor = initialColor;

        this.redWidget = new ColorSliderWithField(screen.getTextRenderer(), SliderType.RED, initialColor);
        this.greenWidget = new ColorSliderWithField(screen.getTextRenderer(), SliderType.GREEN, initialColor);
        this.blueWidget = new ColorSliderWithField(screen.getTextRenderer(), SliderType.BLUE, initialColor);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        super.renderContent(context, mouseX, mouseY);

        this.redWidget.render(context, x, y + 20, mouseX, mouseY);
        this.blueWidget.render(context, x, y + 40, mouseX, mouseY);
        this.greenWidget.render(context, x, y + 60, mouseX, mouseY);
    }

    @Override
    public List<? extends Element> children() {
        return Stream.of(redWidget.getWidgets(), greenWidget.getWidgets(), blueWidget.getWidgets())
                .flatMap(List::stream)
                .toList();
    }

    private static class ColorSliderWithField {
        public static final int SLIDER_WIDTH = 128, SLIDER_HEIGHT = 18, FIELD_WIDTH = 36;

        private static final Predicate<String> COLOR_VALUE_PREDICATE = (text) -> {
            if (text.isEmpty()) {
                return true;
            }

            try {
                int value = Integer.parseInt(text);
                return value >= 0 && value <= 255;
            } catch (NumberFormatException e) {
                return false;
            }
        };

        private final SliderType sliderType;

        private final TextFieldWidget field;
        private final ColorSlider slider;

        public ColorSliderWithField(TextRenderer textRenderer, SliderType sliderType, int initialColor) {
            this.sliderType = sliderType;

            int colorValue = sliderType.colorValue(initialColor);

            this.field = new TextFieldWidget(textRenderer, FIELD_WIDTH, SLIDER_HEIGHT, Text.empty());
            this.field.setText(Integer.toString(colorValue));
            this.field.setTextPredicate(COLOR_VALUE_PREDICATE);

            this.slider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, Text.literal(sliderType.label), colorValue / 255d);

            this.slider.setChangedListener(this::onSliderChanged);
            this.field.setChangedListener(this::onFieldChanged);
        }

        public void onSliderChanged(int value) {
            this.field.setChangedListener(null);
            this.field.setText(Integer.toString(value));
            this.field.setChangedListener(this::onFieldChanged);
        }

        public void onFieldChanged(String value) {
             if (!COLOR_VALUE_PREDICATE.test(value)) {
                return;
            }

            this.slider.setChangedListener(null);
            if (value.isEmpty()) {
                this.slider.setColorValue(0);
            } else {
                this.slider.setColorValue(Integer.parseInt(value));
            }
            this.slider.setChangedListener(this::onSliderChanged);
        }

        public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
            slider.setPosition(x, y);
            slider.render(drawContext, mouseX, mouseY, 0);

            field.setPosition(x + SLIDER_WIDTH, y);
            field.render(drawContext, mouseX, mouseY, 0);
        }

        public List<? extends Element> getWidgets() {
            return List.of(slider, field);
        }
    }

    private enum SliderType {
        // TODO: Move labels to translateables
        RED("Red", (color) -> ColorUtils.toRgba(color)[0]),
        GREEN("Green", (color) -> ColorUtils.toRgba(color)[1]),
        BLUE("Blue", (color) -> ColorUtils.toRgba(color)[2]);

        private final String label;
        private final Function<Integer, Integer> valueFromColor;

        SliderType(String label, Function<Integer, Integer> valueFromColor) {
            this.label = label;
            this.valueFromColor = valueFromColor;
        }

        String getLabel() {
            return this.label;
        }

        int colorValue(int color) {
            return valueFromColor.apply(color);
        }
    }
}
