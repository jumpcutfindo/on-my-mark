package com.jumpcutfindo.onmymark.graphics.screen;

import com.jumpcutfindo.onmymark.graphics.screen.party.PartyMemberListView;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PartyScreen extends OnMyMarkScreen {
    private int x, y;

    private PartyMemberListView partyMemberListView;

    public PartyScreen() {
        super(Text.translatable("onmymark.party.title"));
    }

    @Override
    protected void init() {
        this.x = (this.width - 216) / 2;
        this.y = (this.height - 178) / 2;

        // Recreate the relevant views
        this.partyMemberListView = new PartyMemberListView(this, null, x, y);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.drawBackgroundGradient(context);

        if (this.partyMemberListView != null) {
            this.partyMemberListView.renderBackground(context, mouseX, mouseY);
            this.partyMemberListView.renderItems(context, mouseX, mouseY);
        }
    }
}
