package com.jumpcutfindo.onmymark.client.graphics.screen.components;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.List;
import java.util.function.Consumer;

public class ColorSlider extends SliderWidget {
    private Text label;
    private int colorValue;

    Consumer<Integer> changedListener;

    public ColorSlider(int width, int height, Text label, double value) {
        super(0, 0, width, height, label, value);

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
        return Texts.join(List.of(label, Text.literal(Integer.toString(colorValue))), Text.literal(": "));
    }

    @Override
    protected void applyValue() {
        this.colorValue = (int) (this.value * 255.0d);

        if (this.changedListener != null) {
            this.changedListener.accept(this.colorValue);
        }
    }

    public void setColorValue(int colorValue) {
        this.value = colorValue / 255.0d;
        this.applyValue();
    }

    public int getColorValue() {
        return colorValue;
    }
}
