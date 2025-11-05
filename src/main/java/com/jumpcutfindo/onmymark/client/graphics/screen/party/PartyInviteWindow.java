package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.OnMyMarkClient;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.OnMyMarkButton;
import com.jumpcutfindo.onmymark.client.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public class PartyInviteWindow extends OnMyMarkWindow {
    public static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/party_invitation_window.png");
    public static final int TEXTURE_WIDTH = 256, TEXTURE_HEIGHT = 256;

    public static final int WINDOW_WIDTH = 160, WINDOW_HEIGHT = 128;
    private final OnMyMarkButton acceptButton, rejectButton;

    private final PartyInvite<ClientPartyMember> partyInvite;

    private final Supplier<SkinTextures> inviterSkinTexturesSupplier;

    public PartyInviteWindow(OnMyMarkScreen screen, PartyInvite<ClientPartyMember> partyInvite) {
        super(screen, Text.translatable("gui.onmymark.partyInvite.windowTitle"), WINDOW_WIDTH, WINDOW_HEIGHT);

        this.partyInvite = partyInvite;

        this.acceptButton = new OnMyMarkButton(0, 0, 64, 20, Text.translatable("gui.onmymark.partyInvite.acceptButton"), (widget) -> {
            this.respondToInvitation(true);
            this.screen.setActiveWindow(null);
        });

        this.rejectButton = new OnMyMarkButton(0, 0, 64, 20, Text.translatable("gui.onmymark.partyInvite.rejectButton"), (widget) -> {
            this.respondToInvitation(false);
            this.screen.setActiveWindow(null);
        });

        this.inviterSkinTexturesSupplier = MinecraftClient.getInstance().getSkinProvider()
                .supplySkinTextures(partyInvite.from().gameProfile(), false);
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        TextRenderer textRenderer = this.screen.getTextRenderer();

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(2F, 2F);

        SkinTextures inviterSkinTextures = this.inviterSkinTexturesSupplier.get();
        PlayerSkinDrawer.draw(context, inviterSkinTextures.body().texturePath(), (x + (WINDOW_WIDTH - 24) / 2) / 2, (y + 10) / 2, 12, false, false, -1);
        context.getMatrices().popMatrix();

        Text inviterName = Text.literal(this.partyInvite.from().displayName()).styled(style -> style.withBold(true));
        int inviterNameWidth = textRenderer.getWidth(inviterName);
        context.drawText(this.screen.getTextRenderer(), inviterName, (x + (WINDOW_WIDTH - inviterNameWidth) / 2), (y + 40), Colors.BLACK, false);

        Text invitationMessage = Text.translatable("gui.onmymark.partyInvite.invitationMessage");

        List<OrderedText> lines = textRenderer.wrapLines(invitationMessage, WINDOW_WIDTH - 24);
        int lineOffset = 54;
        for (OrderedText text : lines) {
            context.drawText(textRenderer, text, (x + (WINDOW_WIDTH - textRenderer.getWidth(text)) / 2), y + lineOffset, Colors.BLACK, false);
            lineOffset += textRenderer.fontHeight;
        }

        Text partyName = Text.literal(this.partyInvite.party().partyName()).styled(style -> style.withBold(true).withFormatting(Formatting.BLUE));
        int partyNameWidth = textRenderer.getWidth(partyName);
        context.drawText(this.screen.getTextRenderer(), partyName, (x + (WINDOW_WIDTH - partyNameWidth) / 2), (y + 80), Colors.BLACK, false);

        // Button rendering
        this.rejectButton.setX(x + 10);
        this.rejectButton.setY(y + 98);
        this.rejectButton.render(context, mouseX, mouseY, 0);

        this.acceptButton.setX(x + 86);
        this.acceptButton.setY(y + 98);
        this.acceptButton.render(context, mouseX, mouseY, 0);

    }

    @Override
    public List<? extends Element> children() {
        return List.of(this.acceptButton, this.rejectButton);
    }

    private Text getInviterNameStyled(String partyLeaderName) {
        return Text.literal(partyLeaderName).styled((style) -> style.withColor(TextColor.fromFormatting(Formatting.BLUE)));
    }

    private Text getPartyNameStyled(String partyName) {
        return Text.literal(partyName).styled((style) -> style.withColor(TextColor.fromFormatting(Formatting.RED)));
    }

    private void respondToInvitation(boolean isAccept) {
        ClientNetworkSender.sendInvitationResponse(isAccept);

        // Remove locally stored invite
        OnMyMarkClient.INSTANCE.clientPartyManager().setPartyInvite(null);
    }
}
