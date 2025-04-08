package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockMarker extends Marker {
    private final BlockPos blockPos;
    private final BlockState blockState;

    public BlockMarker(PartyMember owner, BlockPos blockPos, BlockState blockState) {
        super(owner);

        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    public BlockState blockState() {
        return blockState;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    @Override
    public Vec3d getExactPosition() {
        return blockPos.toCenterPos();
    }
}
