package com.jumpcutfindo.onmymark.network.server;

import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public record ServerPacketContext(ServerPlayNetworking.Context networkContext, ServerPartyManager partyManager) {
    public ServerPlayerEntity player() {
        return networkContext.player();
    }

    public MinecraftServer server() {
        return networkContext.server();
    }
}
