package com.jumpcutfindo.onmymark.mixin;

import com.jumpcutfindo.onmymark.server.OnMyMarkServer;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method= "remove", at = @At("HEAD"))
    public void onEntityRemoved(Entity.RemovalReason reason, CallbackInfo callbackInfo) {
        ServerMarkerManager markerManager = OnMyMarkServer.INSTANCE.serverMarkerManager();
        markerManager.removeEntityMarker((Entity) (Object) this);

        if ((Object) this instanceof PlayerEntity player) {
            markerManager.removePlayerMarker(player);
        }
    }
}
