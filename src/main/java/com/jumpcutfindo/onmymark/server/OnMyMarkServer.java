package com.jumpcutfindo.onmymark.server;

import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.marker.ServerMarkerManager;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyUnavailableException;
import com.jumpcutfindo.onmymark.server.party.exceptions.PlayerNotInPartyException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnMyMarkServer implements ModInitializer {
    public static OnMyMarkServer INSTANCE = null;

    private ServerPartyManager serverPartyManager = null;
    private ServerMarkerManager serverMarkerManager = null;

    @Override
    public void onInitialize() {
        INSTANCE = this;

        this.serverPartyManager = new ServerPartyManager();
        this.serverMarkerManager = new ServerMarkerManager();

        ServerPlayConnectionEvents.JOIN.register(this::onPlayerConnected);
        ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerDisconnected);
    }

    public void onPlayerConnected(ServerPlayNetworkHandler networkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayerEntity serverPlayer = networkHandler.getPlayer();

        try {
            Party<ServerPartyMember> party = serverPartyManager.handlePlayerConnected(serverPlayer);
            ServerNetworkSender.sendPartyInfo(party);
        } catch (PlayerNotInPartyException | PartyUnavailableException ignored) {
        }
    }

    public void onPlayerDisconnected(ServerPlayNetworkHandler networkHandler, MinecraftServer minecraftServer) {
        ServerPlayerEntity serverPlayer = networkHandler.getPlayer();

        try {
            Party<ServerPartyMember> party = serverPartyManager.handlePlayerDisconnected(serverPlayer);
            ServerNetworkSender.sendPartyInfo(party);
        } catch (PlayerNotInPartyException ignored) {
        }
    }

    public ServerPartyManager serverPartyManager() {
        return serverPartyManager;
    }

    public ServerMarkerManager serverMarkerManager() {
        return serverMarkerManager;
    }
}