package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockMarker extends Marker {
    private final BlockPos blockPos;

    public BlockMarker(PartyMember owner, BlockPos blockPos) {
        super(owner);
        this.blockPos = blockPos;
    }

    @Override
    public Vec3d getExactPosition() {
        return blockPos.toCenterPos();
    }
}
