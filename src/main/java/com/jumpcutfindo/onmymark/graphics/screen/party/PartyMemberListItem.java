package com.jumpcutfindo.onmymark.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.graphics.screen.components.ListItem;
import com.jumpcutfindo.onmymark.graphics.screen.utils.StringUtils;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PartyMemberListItem extends ListItem<PartyMember> {
    private static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/party_screen.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    public PartyMemberListItem(OnMyMarkScreen screen, PartyMember partyMember, int index) {
        super(screen, partyMember, index);

        this.setBackground(TEXTURE, 0, 178, 180, 18, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        this.item = partyMember;
        this.index = index;
    }

    @Override
    public void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {
        String displayName = this.item.displayName();
        context.drawText(this.screen.getTextRenderer(), Text.literal(StringUtils.truncatedName(displayName, 14)), (x + 6), (y + 5), index % 2 == 0 ? 0xFF000000 : 0xFFFF0000, false);
    }

    @Override
    public boolean mouseClicked(int x, int y, double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean mouseSelected(int x, int y, double mouseX, double mouseY) {
        return false;
    }
}
