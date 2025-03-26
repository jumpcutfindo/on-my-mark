package com.jumpcutfindo.onmymark.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.graphics.screen.components.OnMyMarkButton;
import com.jumpcutfindo.onmymark.network.ClientNetworkSender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class CreatePartyWindow extends OnMyMarkWindow {
    public static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/create_party_window.png");
    public static final int TEXTURE_WIDTH = 256, TEXTURE_HEIGHT = 256;

    public static final int WINDOW_WIDTH = 138, WINDOW_HEIGHT = 92;

    private final TextFieldWidget partyNameField;
    private final OnMyMarkButton submitButton;

    public CreatePartyWindow(OnMyMarkScreen screen) {
        super(screen, getStyledTitle(Text.translatable("onmymark.menu.createParty.windowTitle")), WINDOW_WIDTH, WINDOW_HEIGHT, screen.getWindowX(WINDOW_WIDTH), screen.getWindowY(WINDOW_HEIGHT));

        // Create text field
        this.partyNameField = new TextFieldWidget(screen.getTextRenderer(), 0, 0, 124, 18, Text.translatable("onmymark.menu.createParty.textWidget"));
        this.partyNameField.setMaxLength(20);
        this.partyNameField.setEditableColor(16777215);

        this.submitButton = new OnMyMarkButton(0, 0, 64, 20, Text.translatable("onmymark.menu.createParty.submitButton"), (widget) -> {
            this.createParty();
            this.screen.setActiveWindow(null);
        });
    }

    @Override
    public void renderBackground(DrawContext context) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), 0x404040, false);

        // Party name entry
        context.drawText(this.screen.getTextRenderer(), Text.translatable("onmymark.menu.createParty.partyNameLabel"), (x + this.titleX), (y + 25), 0x404040, false);

        this.partyNameField.setX(x + 7);
        this.partyNameField.setY(y + 36);
        this.partyNameField.render(context, mouseX, mouseY, 0);

        // Button rendering
        this.submitButton.setX(x + 67);
        this.submitButton.setY(y + 64);
        this.submitButton.render(context, mouseX, mouseY, 0);
        this.submitButton.active = this.isValidInput();

        this.partyNameField.setFocused(true);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button)
                || this.partyNameField.mouseClicked(mouseX, mouseY, button)
                || this.submitButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.partyNameField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.partyNameField.charTyped(chr, modifiers);
    }

    @Override
    public List<ClickableWidget> getWidgets() {
        return List.of(this.partyNameField, this.submitButton);
    }

    private boolean isValidInput() {
        return !this.partyNameField.getText().isEmpty() || !this.partyNameField.getText().isBlank();
    }

    private void createParty() {
        ClientNetworkSender.createParty(this.partyNameField.getText());
    }
}
