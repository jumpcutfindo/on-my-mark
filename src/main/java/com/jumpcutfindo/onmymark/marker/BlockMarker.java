package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockMarker extends Marker {
    private final BlockPos blockPos;
    private final Block block;

    private BlockState blockState;

    public BlockMarker(PartyMember owner, RegistryKey<World> worldRegistryKey, BlockPos blockPos, Block block) {
        super(owner, worldRegistryKey, blockPos.toCenterPos());

        this.blockPos = blockPos;
        this.block = block;
    }

    @Override
    public void update(World world) {
        this.blockState = world.getBlockState(blockPos);

        if (blockState.getBlock().equals(Blocks.VOID_AIR)) {
            this.setLiveness(Liveness.DORMANT);
        } else {
            this.setLiveness(Liveness.LIVE);
        }
    }

    public BlockState blockState() {
        return blockState;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public Block block() {
        return block;
    }

    @Override
    public Vec3d getExactPosition(World world) {
        return blockPos.toCenterPos();
    }
}
