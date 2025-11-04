package com.jumpcutfindo.onmymark.client.graphics.screen;

import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ScreenUtils;
import com.jumpcutfindo.onmymark.client.input.InputListener;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Mod specific screen that introduces some helper methods for use throughout the application.
 * Adds support for Windows that can be added on top of the existing interface.
 */
public abstract class OnMyMarkScreen extends Screen {
    OnMyMarkWindow activeWindow;
    boolean isStandalone;

    public OnMyMarkScreen(Text title) {
        super(title);
    }

    public boolean isStandalone() {
        return isStandalone;
    }

    public void setStandalone(boolean standalone) {
        isStandalone = standalone;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.activeWindow != null) {
            this.activeWindow.setPos(this.getWindowX(this.activeWindow.getWidth()), this.getWindowY(this.activeWindow.getHeight()));
            this.activeWindow.render(context, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.activeWindow != null) {
            return this.activeWindow.mouseClicked(click, doubled);
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (this.activeWindow != null) {
            return this.activeWindow.mouseDragged(click, offsetX, offsetY);
        }

        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.activeWindow != null) {
            return this.activeWindow.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.getKeycode() == GLFW.GLFW_KEY_ESCAPE && this.activeWindow != null) {
            if (isStandalone) close();
            this.activeWindow = null;
            return true;
        }

        if (input.getKeycode() == KeyBindingHelper.getBoundKeyOf(InputListener.GUI_BINDING).getCode() && this.activeWindow == null) {
            close();
            return true;
        }

        if (this.activeWindow != null) {
            return this.activeWindow.keyPressed(input);
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (this.activeWindow != null) {
            return this.activeWindow.charTyped(charInput);
        }

        return super.charTyped(charInput);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.activeWindow != null) this.activeWindow.tick();
    }

    public void drawBackgroundGradient(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);

    }

    public OnMyMarkWindow getActiveWindow() {
        return activeWindow;
    }

    public void setActiveWindow(OnMyMarkWindow window) {
        this.clearChildren();
        this.activeWindow = window;

        if (window == null) return;

        // this.activeWindow.getWidgets().forEach(this::addSelectableChild);
    }

    public int getWindowX(int windowWidth) {
        return (this.width - windowWidth) / 2;
    }

    public int getWindowY(int windowHeight) {
        return (this.height - windowHeight) / 2;
    }

    public boolean isWindowOpen() {
        return this.activeWindow != null;
    }

    public boolean isBlockedByWindow(int x, int y) {
        if (this.activeWindow == null) return false;
        else {
            return ScreenUtils.isWithin(x, y, this.activeWindow.getX(), this.activeWindow.getY(), this.activeWindow.getWidth(), this.activeWindow.getHeight());
        }
    }

    public PlayerEntity getPlayer() {
        return client.player;
    }
}
