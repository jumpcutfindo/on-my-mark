package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ColorUtils;
import com.jumpcutfindo.onmymark.client.graphics.utils.DrawUtils;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class BlockMarkerRenderer extends MarkerRenderer {
    private final BlockMarker blockMarker;

    public BlockMarkerRenderer(MinecraftClient client, BlockMarker blockMarker) {
        super(client, blockMarker);

        this.blockMarker = blockMarker;
    }

    @Override
    public void drawLabel(DrawContext drawContext, float screenX, float screenY, boolean isOutlined) {
        ItemStack blockItem = this.blockMarker.block().asItem().getDefaultStack();
        DrawUtils.drawItemOutlined(drawContext, blockItem, (int) screenX, (int) screenY, this.getPointerColor());
    }

    @Override
    public int getLabelWidth() {
        return (int) MarkerRenderer.ICON_SIZE;
    }

    @Override
    public int getLabelHeight() {
        return (int) MarkerRenderer.ICON_SIZE;
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
        return this.blockMarker.blockState().getBlock().getName().getString();
    }

    @Override
    boolean isMoving() {
        return false;
    }

    @Override
    int getPointerColor() {
        return ColorUtils.getColorOfIndex(((ClientPartyMember) this.blockMarker.owner()).getPartyIndex());
    }

    @Override
    public boolean shouldDraw() {
        ClientWorld world = MinecraftClient.getInstance().world;

        return super.shouldDraw() && world != null && !world.getBlockState(this.blockMarker.blockPos()).isAir();
    }
}
