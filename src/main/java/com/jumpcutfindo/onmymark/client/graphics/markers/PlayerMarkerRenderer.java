package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.marker.PlayerMarker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public class PlayerMarkerRenderer extends MarkerRenderer {
    public static final long DEFAULT_LIFETIME_SECS = 60L;
    private static final int PLAYER_HEAD_SIZE = 8;

    private final PlayerMarker playerMarker;

    public PlayerMarkerRenderer(MinecraftClient client, PlayerMarker marker) {
        super(client, marker, PointerShape.DIAMOND);

        this.playerMarker = marker;
    }

    @Override
    public void drawLabel(DrawContext drawContext, float screenX, float screenY, boolean isOutlined) {
        super.drawLabel(drawContext, screenX, screenY, isOutlined);
    }

    @Override
    protected LabelDisplayType getLabelDisplayType() {
        return LabelDisplayType.CUSTOM;
    }

    @Override
    public int getLabelWidth() {
        return client.textRenderer.getWidth(this.getName());
    }

    @Override
    public int getLabelHeight() {
        return client.textRenderer.fontHeight;
    }

    @Override
    public long getLifetimeMs() {
        long configuredLifetime = OnMyMarkMod.CONFIG.playerMarkerLifetimeSecs() * 1000;
        return configuredLifetime <= 0L ? DEFAULT_LIFETIME_SECS * 1000 : configuredLifetime;
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
