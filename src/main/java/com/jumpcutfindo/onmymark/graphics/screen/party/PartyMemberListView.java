package com.jumpcutfindo.onmymark.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.OnMyMarkClientMod;
import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.graphics.screen.components.ListView;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PartyMemberListView extends ListView<PartyMemberListItem> {
    protected static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/party_screen.png");
    protected static final int TEXTURE_WIDTH = 256;
    protected static final int TEXTURE_HEIGHT = 256;

    private static final int MAX_ITEMS = 8;

    private Party party;

    private int titleX, titleY;
    private Text title;

public PartyMemberListView(OnMyMarkScreen screen, Party party, int x, int y) {
        super(screen);

        this.party = party;

        this.title = party != null ? Text.literal(party.partyName()) : Text.translatable("onmymark.modName");
        this.titleX = 7;
        this.titleY = 10;

        this.setPosition(x, y)
                .setTexture(TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0, 216, 178)
                .setListPosition(8, 26)
                .setScrollbar(195, 26, 216, 0, 14, 144)
                .setList(createItems(screen, party), MAX_ITEMS)
                .setSelectType(SelectType.SINGLE);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY) {
        super.renderBackground(context, mouseX, mouseY);

        context.drawText(this.screen.getTextRenderer(), this.title, (x + this.titleX), (y + this.titleY), 0x404040, false);
    }

    @Override
    public NbtCompound getSettings() {
        return null;
    }

    @Override
    public void applySettings(NbtCompound settings) {

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    public void setParty(Party party) {
        this.party = party;

        // Update list of items
        this.title = party != null ? Text.literal(party.partyName()) : Text.translatable("onmymark.modName");
        this.setList(createItems(this.screen, party), MAX_ITEMS);
    }

    private static List<PartyMemberListItem> createItems(OnMyMarkScreen screen, Party party) {
        List<PartyMemberListItem> items = new ArrayList<>();

        if (party == null) {
            return items;
        }

        for (int i = 0; i < party.partyMembers().size(); i++) {
            items.add(new PartyMemberListItem(screen, party.partyMembers().get(i), i));
        }

        return items;
    }

    @Override
    public boolean canSelect() {
        PartyMember partyMember = OnMyMarkClientMod.INSTANCE.clientPartyManager().self();

        if (partyMember == null) {
            return true;
        }
        else {
            return partyMember.isPartyLeader();
        }
    }
}
