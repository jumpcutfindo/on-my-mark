package com.jumpcutfindo.onmymark.graphics.markers;

import com.jumpcutfindo.onmymark.graphics.utils.RenderMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public abstract class MarkerRenderer {
    private MinecraftClient client;

    private Vec3d worldPos, prevWorldPos;

    protected Vector4f screenPos, prevScreenPos;

    protected MarkerRenderer(MinecraftClient client) {
        this.client = client;

        this.screenPos = null;
        this.prevScreenPos = null;
    }

    public void renderTick(float tickDelta, float fovDegrees, boolean isFovChanging) {
        // Calculate screen position
        prevScreenPos = screenPos;

        Vec3d markerPos = this.getMarkerWorldPos();
        Vector4f newScreenPos = RenderMath.worldToScreenPos(client, markerPos, fovDegrees);

        if (newScreenPos == null || prevScreenPos == null) {
            screenPos = newScreenPos;
            return;
        }

        if (!isMoving()) {
            screenPos = newScreenPos;
        } else {
            screenPos = new Vector4f(
                    MathHelper.lerp(tickDelta, prevScreenPos.x(), newScreenPos.x()),
                    MathHelper.lerp(tickDelta, prevScreenPos.y(), newScreenPos.y()),
                    MathHelper.lerp(tickDelta, prevScreenPos.z(), newScreenPos.z()),
                    MathHelper.lerp(tickDelta, prevScreenPos.w(), newScreenPos.w())
            );
        }

    }

    public boolean shouldDraw() {
        return screenPos != null;
    }

    abstract Vec3d getMarkerWorldPos();

    abstract boolean isMoving();

    public abstract void draw(DrawContext drawContext);
}
