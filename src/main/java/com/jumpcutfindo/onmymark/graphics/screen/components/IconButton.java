package com.jumpcutfindo.onmymark.graphics.screen.components;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.graphics.screen.utils.ScreenUtils;
import com.jumpcutfindo.onmymark.graphics.screen.utils.SoundUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

public class IconButton implements Interactable {
    private static final Identifier BUTTONS_TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/buttons.png");
    private static final int BUTTONS_TEXTURE_WIDTH = 256;
    private static final int BUTTONS_TEXTURE_HEIGHT = 256;
    
    private final OnMyMarkScreen screen;
    private final int x, y;
    private final int u, v;
    private final int width, height;

    private boolean active, disabled;
    private final Runnable action;

    private final MutableText tooltip;

    public IconButton(OnMyMarkScreen screen, int x, int y, int u, int v, Runnable action, MutableText tooltip) {
        this.screen = screen;

        this.x = x;
        this.y = y;

        this.u = u;
        this.v = v;

        this.width = 16;
        this.height = 16;

        this.disabled = false;

        this.action = action;
        this.tooltip = tooltip;
    }

    public void render(DrawContext context, int mouseX, int mouseY, int delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (isDisabled()) {
            // Disabled
            this.drawDefaultBase(context);
            this.drawIcon(context);
            this.drawDisabledOverlay(context);
            return;
        }

        if (isActive()) {
            this.drawActiveBase(context);
            this.drawIcon(context);
        }

        if (isMouseWithin(mouseX, mouseY) && !screen.isWindowOpen()) {
            // Hovered
            this.drawHoveredBase(context);
            this.drawIcon(context);
        } else {
            // Default
            this.drawDefaultBase(context);
            this.drawIcon(context);
        }
    }

    public boolean renderTooltip(DrawContext context, int mouseX, int mouseY, int delta) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (isMouseWithin(mouseX, mouseY)) {
            context.drawTooltip(this.screen.getTextRenderer(), tooltip, mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!isDisabled() && isMouseWithin(mouseX, mouseY)) {
            SoundUtils.playClickSound(MinecraftClient.getInstance().getSoundManager());
            action.run();
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    private boolean isMouseWithin(int mouseX, int mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, x, y, this.width, this.height);
    }

    private void drawDefaultBase(DrawContext drawContext) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, BUTTONS_TEXTURE, x, y, 0, 0, width, height, BUTTONS_TEXTURE_WIDTH, BUTTONS_TEXTURE_HEIGHT);
    }

    private void drawActiveBase(DrawContext drawContext) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, BUTTONS_TEXTURE, x, y, 32, 0, width, height, BUTTONS_TEXTURE_WIDTH, BUTTONS_TEXTURE_HEIGHT);
    }

    private void drawDisabledBase(DrawContext drawContext) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, BUTTONS_TEXTURE, x, y, 0, 0, width, height, BUTTONS_TEXTURE_WIDTH, BUTTONS_TEXTURE_HEIGHT);
    }

    private void drawHoveredBase(DrawContext drawContext) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, BUTTONS_TEXTURE, x, y, 16, 0, width, height, BUTTONS_TEXTURE_WIDTH, BUTTONS_TEXTURE_HEIGHT);
    }

    private void drawDisabledOverlay(DrawContext drawContext) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, BUTTONS_TEXTURE, x, y, 48, 0, width, height, BUTTONS_TEXTURE_WIDTH, BUTTONS_TEXTURE_HEIGHT);
    }

    private void drawIcon(DrawContext drawContext) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, BUTTONS_TEXTURE, x, y, u, v, width, height, BUTTONS_TEXTURE_WIDTH, BUTTONS_TEXTURE_HEIGHT);
    }
}
