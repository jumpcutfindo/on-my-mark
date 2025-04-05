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

    public void renderTick(DrawContext drawContext, float tickDelta, float fovDegrees, boolean isFovChanging) {
        // Calculate screen position
        prevScreenPos = screenPos;

        Vec3d markerPos = this.getMarkerWorldPos();

        Vector4f newScreenPos = RenderMath.aheadWorldToScreenPos(client, markerPos, fovDegrees);

        if (prevScreenPos == null) {
            screenPos = newScreenPos;
            return;
        }

        if (isMoving()) {
            // Lerp to smooth movement snapping for markers
            screenPos = new Vector4f(
                    MathHelper.lerp(tickDelta, prevScreenPos.x(), newScreenPos.x()),
                    MathHelper.lerp(tickDelta, prevScreenPos.y(), newScreenPos.y()),
                    MathHelper.lerp(tickDelta, prevScreenPos.z(), newScreenPos.z()),
                    MathHelper.lerp(tickDelta, prevScreenPos.w(), newScreenPos.w())
            );
        } else {
            screenPos = newScreenPos;
        }

        screenPos = clampScreenPosToCircle(drawContext, screenPos);
    }

    private Vector4f clampScreenPosToCircle(DrawContext drawContext, Vector4f actualScreenPos) {
        // Bind the screen pos to a circle
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();
        float radius = Math.min(windowWidth, windowHeight) / 3f;

        float centerX = windowWidth / 2f;
        float centerY = windowHeight / 2f;

        float dX = actualScreenPos.x() - centerX;
        float dY = actualScreenPos.y() - centerY;
        float distance = MathHelper.sqrt(dX * dX + dY * dY);

        if (distance > radius || actualScreenPos.w() <= 0) {
            // Out of bounds
            float angle = (float) Math.atan2(dY, dX);
            float clampedX, clampedY;

            clampedX = centerX + MathHelper.cos(angle) * radius;
            clampedY = centerY + MathHelper.sin(angle) * radius;

            return new Vector4f(
                    clampedX, clampedY, screenPos.z(), screenPos.w()
            );
        }

        return actualScreenPos;
    }

    public boolean shouldDraw() {
        return screenPos != null;
    }

    abstract Vec3d getMarkerWorldPos();

    abstract boolean isMoving();

    public abstract void draw(DrawContext drawContext);
}
