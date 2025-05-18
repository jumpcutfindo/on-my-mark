package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.client.graphics.utils.DrawUtils;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class BlockMarkerRenderer extends MarkerRenderer {
    private final BlockMarker blockMarker;

    private final boolean hasIcon;

    public BlockMarkerRenderer(MinecraftClient client, BlockMarker blockMarker) {
        super(client, blockMarker, PointerShape.TRIANGLE);

        this.blockMarker = blockMarker;

        ItemStack blockItem = this.blockMarker.block().asItem().getDefaultStack();
        this.hasIcon = !blockItem.isEmpty();
    }

    @Override
    public void drawLabel(DrawContext drawContext, float screenX, float screenY, boolean isOutlined) {
        ItemStack blockItem = this.blockMarker.block().asItem().getDefaultStack();

        if (blockItem.isEmpty()) {
            super.drawLabel(drawContext, screenX, screenY, isOutlined);
        } else {

            DrawUtils.drawItemOutlined(drawContext, blockItem, (int) screenX, (int) screenY, this.getPointerColor());
        }

    }

    @Override
    protected LabelDisplayType getLabelDisplayType() {
        return this.hasIcon ? LabelDisplayType.ICON : LabelDisplayType.TEXT;
    }

    @Override
    public long getLifetimeMs() {
        // TODO: Change to server configurable value
        return 120000L;
    }

    @Override
    Vec3d getWorldPos() {
        return blockMarker.getExactPosition(this.client.world);
    }

    @Override
    Vec3d getMarkerWorldPos() {
        Vec3d worldPos = blockMarker.getExactPosition(this.client.world);
        return worldPos.add(new Vec3d(0.0f, 1.0f, 0.0f));
    }

    @Override
    String getName() {
        return this.blockMarker.block().getName().getString();
    }

    @Override
    boolean isMoving() {
        return false;
    }

    @Override
    int getPointerColor() {
        return blockMarker.owner().color();
    }

    @Override
    public boolean shouldDraw() {
        return super.shouldDraw();
    }
}
