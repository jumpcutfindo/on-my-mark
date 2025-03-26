package com.jumpcutfindo.onmymark.graphics.screen.party;

import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.graphics.screen.components.IconButton;
import com.jumpcutfindo.onmymark.graphics.screen.utils.ScreenUtils;
import com.jumpcutfindo.onmymark.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.party.Party;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PartyScreen extends OnMyMarkScreen {
    private int x, y;

    private PartyMemberListView partyMemberListView;
    private IconButton createPartyButton, leavePartyButton, invitePlayerButton;

    private Party party;

    public PartyScreen(Party party) {
        super(party != null ? Text.literal(party.partyName()) : Text.translatable("onmymark.modName"));

        this.party = party;
    }

    @Override
    protected void init() {
        this.x = (this.width - 216) / 2;
        this.y = (this.height - 178) / 2;

        // Recreate the relevant views
        this.partyMemberListView = new PartyMemberListView(this, this.party, x, y);

        this.createPartyButton = new IconButton(this, x + 192, y + 6, 0, 16, this::onCreateParty, Text.translatable("onmymark.menu.createParty.tooltip"));
        this.leavePartyButton = new IconButton(this, x + 192, y + 6, 16, 16, this::onLeaveParty, Text.translatable("onmymark.menu.leaveParty.tooltip"));
        this.invitePlayerButton = new IconButton(this, x + 174, y + 6, 32, 16, this::onInvitePlayer, Text.translatable("onmymark.menu.invitePlayer.tooltip"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackgroundGradient(context);

        if (this.partyMemberListView != null) {
            this.partyMemberListView.renderBackground(context, mouseX, mouseY);
            this.partyMemberListView.renderItems(context, mouseX, mouseY);
        }

        this.drawButtons(context, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawButtons(DrawContext context, int mouseX, int mouseY) {
        // Render buttons
        if (this.party == null) {
            this.createPartyButton.render(context, mouseX, mouseY, 0);
        } else {
            this.invitePlayerButton.render(context, mouseX, mouseY, 0);
            this.leavePartyButton.render(context, mouseX, mouseY, 0);
        }

        // Render button tooltips
        if (!this.isWindowOpen()) {
            if (this.party == null) {
                this.createPartyButton.renderTooltip(context, mouseX, mouseY, 0);
            } else {
                this.invitePlayerButton.renderTooltip(context, mouseX, mouseY, 0);
                this.leavePartyButton.renderTooltip(context, mouseX, mouseY, 0);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isWindowOpen()) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (isMouseInList(mouseX, mouseY)) {
            return this.partyMemberListView.mouseClicked((int) mouseX, (int) mouseY, button);
        }

        if (this.party == null) {
            return this.createPartyButton.mouseClicked((int) mouseX, (int) mouseY, button);
        } else {
            return this.invitePlayerButton.mouseClicked((int) mouseX, (int) mouseY, button) || this.leavePartyButton.mouseClicked((int) mouseX, (int) mouseY, button);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isMouseInList(mouseX, mouseY)) {
            return this.partyMemberListView.mouseDragged((int) mouseX, (int) mouseY, button, deltaX, deltaY);
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isMouseInList(mouseX, mouseY)) {
            return this.partyMemberListView.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void setParty(Party party) {
        this.party = party;
        this.partyMemberListView.setParty(party);
    }

    private boolean isMouseInList(double mouseX, double mouseY) {
        return ScreenUtils.isWithin(mouseX, mouseY, this.x + 7, this.y + 25, this.x + 189, this.y + 171);
    }

    private void onCreateParty() {
        // Delegate action to the window
        this.setActiveWindow(new CreatePartyWindow(this));
    }

    private void onLeaveParty() {
        ClientNetworkSender.leaveParty();
    }

    private void onInvitePlayer() {
        this.setActiveWindow(new InvitePlayerWindow(this));
    }
}
