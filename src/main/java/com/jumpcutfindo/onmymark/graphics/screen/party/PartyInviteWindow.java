package com.jumpcutfindo.onmymark.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.OnMyMarkClientMod;
import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkWindow;
import com.jumpcutfindo.onmymark.graphics.screen.components.OnMyMarkButton;
import com.jumpcutfindo.onmymark.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class PartyInviteWindow extends OnMyMarkWindow {
    public static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/party_invitation_window.png");
    public static final int TEXTURE_WIDTH = 256, TEXTURE_HEIGHT = 256;

    public static final int WINDOW_WIDTH = 160, WINDOW_HEIGHT = 92;
    private final OnMyMarkButton acceptButton, rejectButton;

    private final PartyInvite<ClientPartyMember> partyInvite;

    public PartyInviteWindow(OnMyMarkScreen screen, PartyInvite<ClientPartyMember> partyInvite) {
        super(screen, Text.translatable("onmymark.menu.partyInvite.windowTitle"), WINDOW_WIDTH, WINDOW_HEIGHT);

        this.partyInvite = partyInvite;

        this.acceptButton = new OnMyMarkButton(0, 0, 64, 20, Text.translatable("onmymark.menu.partyInvite.acceptButton"), (widget) -> {
            this.respondToInvitation(true);
            this.screen.setActiveWindow(null);
        });

        this.rejectButton = new OnMyMarkButton(0, 0, 64, 20, Text.translatable("onmymark.menu.partyInvite.rejectButton"), (widget) -> {
            this.respondToInvitation(false);
            this.screen.setActiveWindow(null);
        });
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderContent(DrawContext context, int mouseX, int mouseY) {
        super.renderContent(context, mouseX, mouseY);
        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), 0x404040, false);

        context.drawWrappedText(
                this.screen.getTextRenderer(),
                Text.translatable(
                        "onmymark.menu.partyInvite.invitationMessage",
                        this.getInviterNameStyled(partyInvite.from().displayName()), this.getPartyNameStyled(partyInvite.party().partyName())),
                (x + this.titleX),
                (y + 25),
                WINDOW_WIDTH,
                0x404040,
                false
        );

        // Button rendering
        this.acceptButton.setX(x + 10);
        this.acceptButton.setY(y + 64);
        this.acceptButton.render(context, mouseX, mouseY, 0);

        this.rejectButton.setX(x + 86);
        this.rejectButton.setY(y + 64);
        this.rejectButton.render(context, mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button)
                || this.acceptButton.mouseClicked(mouseX, mouseY, button)
                || this.rejectButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public List<ClickableWidget> getWidgets() {
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
        OnMyMarkClientMod.INSTANCE.clientPartyManager().setPartyInvite(null);
    }
}
