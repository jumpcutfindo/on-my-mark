package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.OnMyMarkButton;
import com.jumpcutfindo.onmymark.client.network.ClientNetworkSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class InvitePlayerWindow extends OnMyMarkWindow {
    public static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/invite_player_window.png");
    public static final int TEXTURE_WIDTH = 256, TEXTURE_HEIGHT = 256;

    public static final int WINDOW_WIDTH = 138, WINDOW_HEIGHT = 92;

    private final TextFieldWidget playerNameField;
    private final OnMyMarkButton submitButton;

    public InvitePlayerWindow(OnMyMarkScreen screen) {
        super(screen, Text.translatable("gui.onmymark.invitePlayer.windowTitle"), WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create text field
        this.playerNameField = new TextFieldWidget(screen.getTextRenderer(), 0, 0, 124, 18, Text.translatable("gui.onmymark.invitePlayer.textWidget"));
        this.playerNameField.setMaxLength(20);
        this.playerNameField.setEditableColor(16777215);

        this.submitButton = new OnMyMarkButton(0, 0, 64, 20, Text.translatable("gui.onmymark.invitePlayer.submitButton"), (widget) -> {
            this.createParty();
            this.screen.setActiveWindow(null);
        });

        this.setFocused(this.playerNameField);
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        super.renderContent(context, mouseX, mouseY);

        // Party name entry
        context.drawText(MinecraftClient.getInstance().textRenderer, Text.translatable("gui.onmymark.invitePlayer.playerNameLabel"), (x + this.titleX), (y + 25), 0x404040, false);

        this.playerNameField.setX(x + 7);
        this.playerNameField.setY(y + 36);
        this.playerNameField.render(context, mouseX, mouseY, 0);

        // Button rendering
        this.submitButton.setX(x + 67);
        this.submitButton.setY(y + 64);
        this.submitButton.render(context, mouseX, mouseY, 0);
        this.submitButton.active = this.isValidInput();

        this.playerNameField.setFocused(true);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(this.playerNameField, this.submitButton);
    }

    private boolean isValidInput() {
        return !this.playerNameField.getText().isEmpty() || !this.playerNameField.getText().isBlank();
    }

    private void createParty() {
        ClientNetworkSender.inviteToParty(this.playerNameField.getText());
    }
}
