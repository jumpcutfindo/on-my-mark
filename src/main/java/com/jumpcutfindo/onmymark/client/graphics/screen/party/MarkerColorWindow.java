package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.ColorSlider;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.OnMyMarkButton;
import com.jumpcutfindo.onmymark.client.network.ClientNetworkSender;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MarkerColorWindow extends OnMyMarkWindow {
    public static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/marker_color_window.png");
    public static final int TEXTURE_WIDTH = 256, TEXTURE_HEIGHT = 256;

    public static final int WINDOW_WIDTH = 178, WINDOW_HEIGHT = 153;

    private final int initialColor;

    private final ButtonWidget submitButton;
    private final ColorSliderWithField redWidget, greenWidget, blueWidget;

    public MarkerColorWindow(OnMyMarkScreen screen, int initialColor) {
        super(screen, Text.translatable("gui.onmymark.selectMarkerColor.windowTitle"), WINDOW_WIDTH, WINDOW_HEIGHT);

        this.initialColor = initialColor;

        this.submitButton = new OnMyMarkButton(0, 0, 64, 20, Text.translatable("gui.onmymark.selectMarkerColor.submitButton"), (widget) -> {
            this.screen.setActiveWindow(null);
            this.onSelectMarkerColor();
        });

        this.redWidget = new ColorSliderWithField(screen.getTextRenderer(), SliderType.RED, initialColor);
        this.greenWidget = new ColorSliderWithField(screen.getTextRenderer(), SliderType.GREEN, initialColor);
        this.blueWidget = new ColorSliderWithField(screen.getTextRenderer(), SliderType.BLUE, initialColor);
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        super.renderContent(context, mouseX, mouseY);

        int color = this.getSelectedColor();

        // Draw color preview
        context.fill(x + 7, y + 21, x + 171, y + 59, color);

        int widgetX = x + 6;
        int widgetY = y + 64;

        this.redWidget.render(context, widgetX, widgetY, mouseX, mouseY);
        this.greenWidget.render(context, widgetX, widgetY + 20, mouseX, mouseY);
        this.blueWidget.render(context, widgetX, widgetY + 40, mouseX, mouseY);

        this.submitButton.setPosition(x + 108, y + 126);
        this.submitButton.render(context, mouseX, mouseY, 0);
    }

    private void onSelectMarkerColor() {
        ClientNetworkSender.updateMarkerColor(this.getSelectedColor());
    }

    @Override
    public List<? extends Element> children() {
        return Stream.of(redWidget.getWidgets(), greenWidget.getWidgets(), blueWidget.getWidgets(), List.of(submitButton))
                .flatMap(List::stream)
                .toList();
    }

    public int getSelectedColor() {
        return ColorHelper.getArgb(this.redWidget.getColorValue(), this.greenWidget.getColorValue(), this.blueWidget.getColorValue());
    }

    private enum SliderType {
        RED("gui.onmymark.selectMarkerColor.red", ColorHelper::getRed),
        GREEN("gui.onmymark.selectMarkerColor.green", ColorHelper::getGreen),
        BLUE("gui.onmymark.selectMarkerColor.blue", ColorHelper::getBlue);

        private final String labelKey;
        private final Function<Integer, Integer> valueFromColor;

        SliderType(String labelKey, Function<Integer, Integer> valueFromColor) {
            this.labelKey = labelKey;
            this.valueFromColor = valueFromColor;
        }

        Text getLabel() {
            return Text.translatable(labelKey);
        }

        int colorValue(int color) {
            return valueFromColor.apply(color);
        }
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

            this.slider = new ColorSlider(SLIDER_WIDTH, SLIDER_HEIGHT, sliderType.getLabel(), colorValue / 255d);

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

            field.setPosition(x + SLIDER_WIDTH + 2, y);
            field.render(drawContext, mouseX, mouseY, 0);
        }

        public List<? extends Element> getWidgets() {
            return List.of(slider, field);
        }

        public int getColorValue() {
            return this.slider.getColorValue();
        }
    }
}
