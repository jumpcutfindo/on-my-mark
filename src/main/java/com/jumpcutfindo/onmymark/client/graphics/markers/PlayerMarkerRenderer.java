package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.marker.PlayerMarker;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.math.Vec3d;

public class PlayerMarkerRenderer extends MarkerRenderer {
    private static final int PLAYER_HEAD_SIZE = 8;

    private final PlayerMarker playerMarker;
    private final SkinTextures playerSkinTextures;

    public PlayerMarkerRenderer(MinecraftClient client, PlayerMarker marker) {
        super(client, marker, PointerShape.DIAMOND);

        this.playerMarker = marker;

        this.playerSkinTextures = MinecraftClient.getInstance()
                .getSkinProvider()
                .getSkinTextures(
                        new GameProfile(playerMarker.playerId(), playerMarker.playerName())
                );
    }

    @Override
    public void drawLabel(DrawContext drawContext, float screenX, float screenY, boolean isOutlined) {
        PlayerSkinDrawer.draw(drawContext, this.playerSkinTextures, (int) screenX, (int) screenY, PLAYER_HEAD_SIZE);
        super.drawLabel(drawContext, screenX + PLAYER_HEAD_SIZE + 4, screenY, isOutlined);
    }

    @Override
    protected LabelDisplayType getLabelDisplayType() {
        return LabelDisplayType.CUSTOM;
    }

    @Override
    public int getLabelWidth() {
        return client.textRenderer.getWidth(this.getName()) + PLAYER_HEAD_SIZE + 4;
    }

    @Override
    public int getLabelHeight() {
        return client.textRenderer.fontHeight;
    }

    @Override
    public long getLifetimeMs() {
        // TODO: Change to server configurable value
        return 60000L;
    }

    @Override
    Vec3d getWorldPos() {
        return this.playerMarker.getExactPosition(this.client.world);
    }

    @Override
    Vec3d getMarkerWorldPos() {
        if (MinecraftClient.isHudEnabled()) {
            return getWorldPos().add(0.0F, 2.5F, 0.0F);
        }

        return getWorldPos().add(0.0F, 2.0F, 0.0F);
    }

    @Override
    String getName() {
        return this.playerMarker.playerName();
    }

    @Override
    int getPointerColor() {
        return playerMarker.owner().color();
    }

    @Override
    boolean isMoving() {
        if (this.playerMarker.liveness() == Marker.Liveness.DORMANT) {
            return false;
        }

        Vec3d velocity = playerMarker.player().getVelocity();
        return !velocity.equals(Vec3d.ZERO);
    }

    @Override
    public boolean shouldDraw() {
        return !this.playerMarker.playerId().equals(this.client.player.getUuid()) && super.shouldDraw();
    }
}
