package com.jumpcutfindo.onmymark.mixin;

import com.jumpcutfindo.onmymark.server.OnMyMarkServer;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "onStateReplaced", at = @At(value = "HEAD"))
    private void onBlockStateChanged(BlockState state, ServerWorld world, BlockPos pos, boolean moved, CallbackInfo callbackInfo) {
        ServerMarkerManager markerManager = OnMyMarkServer.INSTANCE.serverMarkerManager();
        markerManager.removeBlockMarker(pos);
    }
}
