package com.jumpcutfindo.onmymark.graphics.markers;

import com.jumpcutfindo.onmymark.graphics.utils.RenderMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public abstract class MarkerRenderer {
    private MinecraftClient client;
    protected Vector4f screenPos, prevScreenPos;

    protected MarkerRenderer(MinecraftClient client) {
        this.client = client;

        this.screenPos = null;
        this.prevScreenPos = null;
    }

    public void renderTick(float tickDelta, float fovMultiplier) {
        prevScreenPos = screenPos;

        Vec3d markerPos = this.getMarkerWorldPos();
        screenPos = RenderMath.worldToScreenPos(client, markerPos, fovMultiplier);
    }

    public boolean shouldDraw() {
        return screenPos != null;
    }

    abstract Vec3d getMarkerWorldPos();

    public abstract void draw(DrawContext drawContext);
}
