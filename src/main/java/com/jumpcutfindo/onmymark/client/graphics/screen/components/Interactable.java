package com.jumpcutfindo.onmymark.client.graphics.screen.components;

public interface Interactable {
    boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);

    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    boolean mouseClicked(int mouseX, int mouseY, int button);

    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    boolean charTyped(char chr, int modifiers);
}
