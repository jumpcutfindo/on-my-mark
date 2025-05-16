package com.jumpcutfindo.onmymark.client.graphics.screen.party;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.graphics.screen.OnMyMarkScreen;
import com.jumpcutfindo.onmymark.client.graphics.screen.components.ListItem;
import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ColorUtils;
import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ScreenUtils;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.utils.StringUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PartyMemberListItem extends ListItem<ClientPartyMember> {
    private static final Identifier TEXTURE = Identifier.of(OnMyMarkMod.MOD_ID, "textures/gui/party_screen.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private final SkinTextures playerSkinTextures;

    public PartyMemberListItem(OnMyMarkScreen screen, ClientPartyMember partyMember, int index) {
        super(screen, partyMember, index);

        this.setBackground(TEXTURE, 0, 178, 180, 18, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        this.item = partyMember;
        this.index = index;

        this.playerSkinTextures = MinecraftClient.getInstance()
                .getSkinProvider()
                .getSkinTextures(
                        partyMember.gameProfile()
                );
    }

    @Override
    public void render(DrawContext context, int x, int y, int mouseX, int mouseY) {
        this.renderBackground(context, x, y, mouseX, mouseY);
        if (this.item.isOffline()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
            this.renderContent(context, x, y, mouseX, mouseY);
        } else {
            this.renderContent(context, x, y, mouseX, mouseY);
        }
    }

    @Override
    public void renderContent(DrawContext context, int x, int y, int mouseX, int mouseY) {
        Text displayName = Text.literal(StringUtils.truncatedName(this.item.displayName(), 14));

        // Draw marker color
        int markerColor = this.item.color();
        context.fill(x + 3, y + 3, x + 8, y + 15, 0xFF000000);
        context.fill(x + 4, y + 4, x + 7, y + 14, markerColor);

        // Draw player icon
        if (this.playerSkinTextures != null) {
            PlayerSkinDrawer.draw(context, this.playerSkinTextures.texture(), x + 11, y + 3, 12, false, false, -1);
        }

        // Draw crown if party leader
        if (this.item.isPartyLeader()) {
            int displayNameWidth = this.screen.getTextRenderer().getWidth(displayName);
            int partyLeaderCrownX = x + displayNameWidth + 27, partyLeaderCrownY = y;
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, partyLeaderCrownX, partyLeaderCrownY, 180, 178, 16, 16, TEXTURE_WIDTH, TEXTURE_HEIGHT);

            if (
                    !this.screen.isWindowOpen()
                            && ScreenUtils.isWithin(mouseX, mouseY, partyLeaderCrownX, partyLeaderCrownY, 16, 16)
            ) {
                context.draw();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                context.drawTooltip(this.screen.getTextRenderer(), Text.translatable("onmymark.menu.party.partyLeader"), mouseX, mouseY);
            }
        }

        // Draw name
        context.drawText(this.screen.getTextRenderer(), displayName, (x + 27), (y + 5), 0x282828, false);
    }

    @Override
    public void renderSelectedBackground(DrawContext context, int x, int y, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 196, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public boolean canSelect() {
        return !this.item.isPartyLeader();
    }

    /**
     * Derived from PlayerListEntry#texturesSupplier
     *
     */
    private static Supplier<SkinTextures> texturesSupplier(GameProfile profile) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        PlayerSkinProvider playerSkinProvider = minecraftClient.getSkinProvider();

        CompletableFuture<Optional<SkinTextures>> completableFuture = playerSkinProvider.fetchSkinTextures(profile);
        boolean bl = !minecraftClient.uuidEquals(profile.getId());
        SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(profile);

        return () -> {
            SkinTextures skinTextures2 = completableFuture.getNow(Optional.empty()).orElse(skinTextures);
            return bl && !skinTextures2.secure() ? skinTextures : skinTextures2;
        };
    }
}
