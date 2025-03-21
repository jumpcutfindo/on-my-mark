package com.jumpcutfindo.onmymark.graphics.markers;

import com.jumpcutfindo.onmymark.graphics.utils.ObjectDrawer;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public class BlockMarkerRenderer extends MarkerRenderer {
    private final BlockMarker blockMarker;

    public BlockMarkerRenderer(MinecraftClient client, BlockMarker blockMarker) {
        super(client);

        this.blockMarker = blockMarker;
    }

    @Override
    public void draw(DrawContext drawContext) {
        ObjectDrawer.drawTriangle(drawContext, this.screenPos.x(), this.screenPos.y(), 20, 0xFF0000FF);
    }

    @Override
    Vec3d getMarkerWorldPos() {
        Vec3d worldPos = blockMarker.getExactPosition();
        return worldPos.add(new Vec3d(0.0f, 1.0f, 0.0f));
    }
}
