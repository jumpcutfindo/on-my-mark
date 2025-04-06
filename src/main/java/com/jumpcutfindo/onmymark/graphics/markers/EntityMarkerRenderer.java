package com.jumpcutfindo.onmymark.graphics.markers;

import com.jumpcutfindo.onmymark.graphics.utils.ObjectDrawer;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public class EntityMarkerRenderer extends MarkerRenderer {
    private final EntityMarker entityMarker;

    public EntityMarkerRenderer(MinecraftClient client, EntityMarker entityMarker) {
        super(client);

        this.entityMarker = entityMarker;
    }

    @Override
    public void draw(DrawContext drawContext) {
        super.draw(drawContext);
        ObjectDrawer.drawTriangle(drawContext, this.screenPos.x(), this.screenPos.y(), 30, 0xFFFFFFFF);
    }

    @Override
    public void drawIcon(DrawContext drawContext, float screenX, float screenY) {

    }

    @Override
    Vec3d getMarkerWorldPos() {
        Vec3d worldPos = entityMarker.getExactPosition();

        return worldPos.add(new Vec3d(0.0f, entityMarker.entity().getHeight(), 0.0f));
    }

    @Override
    boolean isMoving() {
        Vec3d velocity = entityMarker.entity().getVelocity();
        return !velocity.equals(Vec3d.ZERO);
    }
}
