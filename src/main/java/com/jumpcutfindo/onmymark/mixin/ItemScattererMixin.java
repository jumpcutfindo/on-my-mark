package com.jumpcutfindo.onmymark.mixin;

import com.jumpcutfindo.onmymark.server.OnMyMarkServer;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import net.minecraft.block.BlockState;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemScatterer.class)
public class ItemScattererMixin {
    @Inject(method = "onStateReplaced", at = @At(value = "HEAD"))
    private static void onStateReplaced(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        ServerMarkerManager markerManager = OnMyMarkServer.INSTANCE.serverMarkerManager();
        markerManager.removeBlockMarker(pos);
    }
}
