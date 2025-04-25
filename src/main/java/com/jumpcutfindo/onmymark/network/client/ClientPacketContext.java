package com.jumpcutfindo.onmymark.network.client;

import com.jumpcutfindo.onmymark.client.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Context that wraps around Minecraft's network context to provide more elements
 * @param networkContext Minecraft's network context
 * @param markerManager Client marker manager
 * @param partyManager Client party manager
 */
public record ClientPacketContext(ClientPlayNetworking.Context networkContext, ClientMarkerManager markerManager, ClientPartyManager partyManager) {
    public ClientPlayerEntity player() {
        return networkContext.player();
    }

    public MinecraftClient client() {
        return networkContext.client();
    }
}
