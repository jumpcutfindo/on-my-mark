package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.network.packets.PartyInfoPacket;
import com.jumpcutfindo.onmymark.network.packets.RemovePartyInfoPacket;
import com.jumpcutfindo.onmymark.party.Party;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
        PayloadTypeRegistry.playS2C().register(RemovePartyInfoPacket.PACKET_ID, RemovePartyInfoPacket.PACKET_CODEC);
    }

    public static void sendPartyInfo(ServerPlayerEntity player, Party party) {
        ServerPlayNetworking.send(player, PartyInfoPacket.fromParty(party));
    }

    public static void removePartyInfo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new RemovePartyInfoPacket(PacketByteBufs.create()));
    }
}
