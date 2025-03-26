package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.network.packets.PartyInfoPacket;
import com.jumpcutfindo.onmymark.party.Party;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworkSender implements ModInitializer {
    @Override
    public void onInitialize() {
        initializeSenders();
    }

    private static void initializeSenders() {
        PayloadTypeRegistry.playS2C().register(PartyInfoPacket.PACKET_ID, PartyInfoPacket.PACKET_CODEC);
    }

    public static void sendPartyInfo(ServerPlayerEntity player, Party party) {
        ServerPlayNetworking.send(player, PartyInfoPacket.fromParty(party));
    }
}
