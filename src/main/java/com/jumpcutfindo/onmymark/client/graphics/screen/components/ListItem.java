package com.jumpcutfindo.onmymark.client.graphics.screen.components;

import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ScreenUtils;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * An item that can be used by ListView. Note that this takes in the x, y position values so that dynamic updating
 * of the position is possible.
 */
public abstract class ListItem<T> {
    protected OnMyMarkScreen screen;
    protected T item;
    protected int index;
    protected int u, v, width, height;
    protected boolean isSelected;
    private Identifier texture;
    private int textureWidth, textureHeight;

    public ListItem(OnMyMarkScreen screen, T item, int index) {
        this.screen = screen;
        this.item = item;
        this.index = index;
    }

    protected void setBackground(Identifier texture, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public T getItem() {
        return item;
    }

    public int getIndex() {
        return index;
    }

    public int getHeight() {
        return height;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void render(DrawContext context, int x, int y, int mouseX, int mouseY) {
        this.renderBackground(context, x, y, mouseX, mouseY);
        this.renderContent(context, x, y, mouseX, mouseY);
    }

    public void renderBackground(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (isSelected) {
            // Selected
            renderSelectedBackground(context, x, y, mouseX, mouseY);
            return;
        }

        if (ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height)) {
            // Hovered
            renderHoveredBackground(context, x, y, mouseX, mouseY);
        } else {
            // Normal
            context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);
        }
    }

    public void renderSelectedBackground(DrawContext context, int x, int y, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    public void renderHoveredBackground(DrawContext context, int x, int y, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    public abstract void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY);

    public boolean mouseClicked(int x, int y, double mouseX, double mouseY) {
        return canSelect() && ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height);
    }

    public boolean canSelect() {
        return true;
    }
}
