package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.network.packets.*;
import com.jumpcutfindo.onmymark.party.Party;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ServerNetworkSender implements ModInitializer {
    @Override
    public void onInitialize() {
        initializeSenders();
    }

    private static void initializeSenders() {
        PayloadTypeRegistry.playS2C().register(PartyInfoPacket.PACKET_ID, PartyInfoPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RemovePartyInfoPacket.PACKET_ID, RemovePartyInfoPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(InviteToPartyResponsePacket.PACKET_ID, InviteToPartyResponsePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(PartyInvitationRequestPacket.PACKET_ID, PartyInvitationRequestPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MarkBlockPacket.PACKET_ID, MarkBlockPacket.PACKET_CODEC);
    }

    public static void sendPartyInfo(ServerPlayerEntity player, Party party) {
        ServerPlayNetworking.send(player, PartyInfoPacket.fromParty(party));
    }

    public static void removePartyInfo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new RemovePartyInfoPacket(PacketByteBufs.create()));
    }

    public static void sendInviteToPartyResponse(ServerPlayerEntity player, boolean isSuccessful) {
        ServerPlayNetworking.send(player,
                isSuccessful ? InviteToPartyResponsePacket.successful() : InviteToPartyResponsePacket.unsuccessful()
        );
    }

    public static void sendInvitationRequest(ServerPlayerEntity player, Party party) {
        ServerPlayNetworking.send(player, PartyInvitationRequestPacket.create(party));
    }

    public static void sendBlockMarker(ServerPlayerEntity player, ServerPlayerEntity markerPlayer, BlockPos blockPos) {
        ServerPlayNetworking.send(player, MarkBlockPacket.create(markerPlayer, blockPos));
    }
}
