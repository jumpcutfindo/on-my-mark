package com.jumpcutfindo.onmymark.client.graphics.screen;

import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public abstract class OnMyMarkWindow extends AbstractParentElement {
    protected OnMyMarkScreen screen;
    protected int x, y;
    protected int width, height;
    protected Text title;
    protected int titleX, titleY;

    public OnMyMarkWindow(OnMyMarkScreen screen, MutableText title, int width, int height) {
        this.screen = screen;
        this.title = getStyledTitle(title);

        this.titleX = 7;
        this.titleY = 9;

        this.width = width;
        this.height = height;

        this.x = screen.getWindowX(width);
        this.y = screen.getWindowY(height);
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        this.renderBackground(context);
        this.renderContent(context, mouseX, mouseY);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void renderBackground(DrawContext context) {
        this.renderBackgroundGradient(context);
    }

    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), 0x404040, false);
    }

    public void tick() {

    }

    private void renderBackgroundGradient(DrawContext context) {
        screen.drawBackgroundGradient(context);
    }

    private static Text getStyledTitle(MutableText text) {
        return text.styled((style) -> style.withBold(true));
    }
}
