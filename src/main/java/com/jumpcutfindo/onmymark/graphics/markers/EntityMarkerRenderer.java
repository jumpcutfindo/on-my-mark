package com.jumpcutfindo.onmymark.graphics.markers;

import com.jumpcutfindo.onmymark.graphics.screen.utils.ScreenUtils;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.mixin.VehicleEntityInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.math.Vec3d;

public class EntityMarkerRenderer extends MarkerRenderer {
    private final EntityMarker entityMarker;

    private LabelType labelType;

    public EntityMarkerRenderer(MinecraftClient client, EntityMarker entityMarker) {
        super(client);

        this.entityMarker = entityMarker;
        this.determineLabelType();
    }

    private void determineLabelType() {
        Entity entity = this.entityMarker.entity();

        if (entity instanceof VehicleEntity || entity instanceof ItemEntity) {
            this.labelType = LabelType.ICON;
        } else {
            this.labelType = LabelType.TEXT;
        }
    }

    @Override
    String getName() {
        return this.entityMarker.entity().getName().getString();
    }

    @Override
    Vec3d getWorldPos() {
        return entityMarker.getExactPosition();
    }

    @Override
    Vec3d getMarkerWorldPos() {
        Vec3d worldPos = entityMarker.getExactPosition();

        return worldPos.add(new Vec3d(0.0f, entityMarker.entity().getHeight(), 0.0f));
    }

    @Override
    public void drawLabel(DrawContext drawContext, float screenX, float screenY) {
        if (this.labelType == LabelType.TEXT) {
            super.drawLabel(drawContext, screenX, screenY);
            return;
        }

        Entity entity = this.entityMarker.entity();

        if (entity instanceof ItemEntity item) {
            drawContext.drawItem(item.getStack(), (int) screenX, (int) screenY);
            drawContext.drawStackOverlay(MinecraftClient.getInstance().textRenderer, item.getStack(), (int) screenX, (int) screenY);
        } else if (entity instanceof VehicleEntity vehicle) {
            drawContext.drawItem(((VehicleEntityInvoker) vehicle).asItem().getDefaultStack(), (int) screenX, (int) screenY);
        }
    }

    @Override
    boolean isMoving() {
        Vec3d velocity = entityMarker.entity().getVelocity();
        return !velocity.equals(Vec3d.ZERO);
    }

    @Override
    int getPointerColor() {
        return ScreenUtils.getColorOfIndex(this.entityMarker.owner().getPartyIndex());
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw() && !this.entityMarker.entity().isRemoved();
    }

    @Override
    public int getLabelWidth() {
        return this.labelType == LabelType.ICON ? (int) MarkerRenderer.ICON_SIZE : super.getLabelWidth();
    }

    @Override
    public int getLabelHeight() {
        return this.labelType == LabelType.ICON ? (int) MarkerRenderer.ICON_SIZE : super.getLabelHeight();
    }

    public enum LabelType {
        TEXT, ICON
    }
}
