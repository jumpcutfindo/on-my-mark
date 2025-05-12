package com.jumpcutfindo.onmymark.client.graphics.screen.components;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ColorSlider extends SliderWidget {
    private Text label;
    private int colorValue;

    Consumer<Integer> changedListener;

    public ColorSlider(int width, int height, Text label, double value) {
        super(0, 0, width, height, Text.literal(""), value);

        this.label = label;
        this.colorValue = (int) (this.value * 255);
    }

    public void setChangedListener(Consumer<Integer> changedListener) {
        this.changedListener = changedListener;
    }

    @Override
    protected void updateMessage() {

    }

    @Override
    public Text getMessage() {
        return Text.literal(String.format("%s: %d", label.getLiteralString(), this.colorValue));
    }

    @Override
    protected void applyValue() {
        int newValue = (int) (this.value * 255);

        this.setColorValue(newValue);

        if (this.changedListener != null) {
            this.changedListener.accept(newValue);
        }
    }

    public void setColorValue(int colorValue) {
        this.colorValue = colorValue;
    }

    public int getColorValue() {
        return colorValue;
    }
}
