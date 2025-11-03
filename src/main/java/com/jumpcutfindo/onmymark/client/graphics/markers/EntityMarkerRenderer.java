package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.graphics.utils.DrawUtils;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.mixin.VehicleEntityMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.math.Vec3d;

public class EntityMarkerRenderer extends MarkerRenderer {
    public static final long DEFAULT_LIFETIME_SECS = 120L;

    private final EntityMarker entityMarker;

    private final LabelDisplayType labelDisplayType;

    public EntityMarkerRenderer(MinecraftClient client, EntityMarker entityMarker) {
        super(client, entityMarker, PointerShape.TRIANGLE);

        this.entityMarker = entityMarker;

        this.labelDisplayType = this.determineLabelType();
    }

    private LabelDisplayType determineLabelType() {
        Entity entity = entityMarker.entity();

        if (entity instanceof VehicleEntity || entity instanceof ItemEntity) {
            return LabelDisplayType.ICON;
        } else {
            return LabelDisplayType.TEXT;
        }
    }

    @Override
    String getName() {
        return this.entityMarker.entityName();
    }

    @Override
    Vec3d getWorldPos() {
        return entityMarker.getExactPosition(this.client.world);
    }

    @Override
    Vec3d getMarkerWorldPos() {
        if (entityMarker.liveness() == Marker.Liveness.DORMANT) {
            return entityMarker.lastPos();
        }

        Vec3d worldPos = entityMarker.getExactPosition(this.client.world);

        if (this.entityMarker.entity() instanceof ItemEntity) {
            return worldPos.add(new Vec3d(0.0F, 1.0F, 0.0F));
        }

        return worldPos.add(new Vec3d(0.0f, entityMarker.entity().getHeight(), 0.0f));
    }

    @Override
    public void drawLabel(DrawContext drawContext, float screenX, float screenY, boolean isOutlined) {
        // By default, if we can't retrieve the entity, we draw a text label
        if (this.entityMarker.liveness() == Marker.Liveness.DORMANT) {
            super.drawLabel(drawContext, screenX, screenY, isOutlined);
            return;
        }

        Entity entity = this.entityMarker.entity();

        if (entity instanceof ItemEntity item) {
            DrawUtils.drawItemTiled(drawContext, item.getStack(), (int) screenX, (int) screenY, this.getPointerColor());
            drawContext.drawStackOverlay(MinecraftClient.getInstance().textRenderer, item.getStack(), (int) screenX, (int) screenY);
        } else if (entity instanceof VehicleEntity vehicle) {
            DrawUtils.drawItemTiled(drawContext, ((VehicleEntityMixin) vehicle).asItem().getDefaultStack(), (int) screenX, (int) screenY, this.getPointerColor());
        } else {
            super.drawLabel(drawContext, screenX, screenY, isOutlined);
        }
    }

    @Override
    protected LabelDisplayType getLabelDisplayType() {
        return this.labelDisplayType;
    }

    @Override
    boolean isMoving() {
        if (this.entityMarker.liveness() == Marker.Liveness.DORMANT) {
            return false;
        }

        Vec3d velocity = entityMarker.entity().getVelocity();
        return !velocity.equals(Vec3d.ZERO);
    }

    @Override
    int getPointerColor() {
        return entityMarker.owner().color();
    }

    @Override
    public boolean shouldDraw() {
        // Avoid drawing marker if the marker is placed on the player themselves
        if (this.entityMarker.liveness() == Marker.Liveness.LIVE
                && this.entityMarker.entity().equals(MinecraftClient.getInstance().player)) {
            return false;
        }

        return super.shouldDraw();
    }

    @Override
    public long getLifetimeMs() {
        long configuredLifetime = OnMyMarkMod.CONFIG.entityMarkerLifetimeSecs() * 1000;
        return configuredLifetime <= 0L ? DEFAULT_LIFETIME_SECS * 1000 : configuredLifetime;
    }
}
