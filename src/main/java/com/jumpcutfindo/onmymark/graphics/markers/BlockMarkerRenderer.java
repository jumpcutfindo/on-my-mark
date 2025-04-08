package com.jumpcutfindo.onmymark.graphics.markers;

import com.jumpcutfindo.onmymark.graphics.utils.ObjectDrawer;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;

public class BlockMarkerRenderer extends MarkerRenderer {
    private final BlockMarker blockMarker;

    public BlockMarkerRenderer(MinecraftClient client, BlockMarker blockMarker) {
        super(client);

        this.blockMarker = blockMarker;
    }

    @Override
    public void drawIcon(DrawContext drawContext, float screenX, float screenY) {
        ItemStack blockItem = this.blockMarker.blockState().getBlock().asItem().getDefaultStack();
        drawContext.drawItem(blockItem, (int) screenX, (int) screenY);
    }

    @Override
    Vec3d getMarkerWorldPos() {
        Vec3d worldPos = blockMarker.getExactPosition();
        return worldPos.add(new Vec3d(0.0f, 1.0f, 0.0f));
    }

    @Override
    String getName() {
        return this.blockMarker.blockState().getBlock().getName().getString();
    }

    @Override
    boolean isMoving() {
        return false;
    }
}
