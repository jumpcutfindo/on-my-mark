package com.jumpcutfindo.onmymark.mixin;

import com.jumpcutfindo.onmymark.server.OnMyMarkServer;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "onBreak", at = @At(value = "HEAD"))
    private void onBlockStateChanged(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
        ServerMarkerManager markerManager = OnMyMarkServer.INSTANCE.serverMarkerManager();
        markerManager.removeBlockMarker(pos);
    }
}
