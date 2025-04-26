package com.jumpcutfindo.onmymark.server.network;

import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Context that wraps around Minecraft's network context to provide more elements
 * @param networkContext Minecraft's network context
 * @param partyManager Server party manager
 * @param markerManager Server marker manager
 */
public record ServerPacketContext(ServerPlayNetworking.Context networkContext, ServerPartyManager partyManager, ServerMarkerManager markerManager) {
    public ServerPlayerEntity player() {
        return networkContext.player();
    }

    public MinecraftServer server() {
        return networkContext.server();
    }
}
