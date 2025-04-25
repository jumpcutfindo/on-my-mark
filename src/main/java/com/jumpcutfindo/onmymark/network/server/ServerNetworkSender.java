package com.jumpcutfindo.onmymark.network.server;

import com.jumpcutfindo.onmymark.network.packets.*;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ServerNetworkSender implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(PartyInfoPacket.PACKET_ID, PartyInfoPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RemovePartyInfoPacket.PACKET_ID, RemovePartyInfoPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(InvitePlayerInvitationPacket.PACKET_ID, InvitePlayerInvitationPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(InvitePlayerResultPacket.PACKET_ID, InvitePlayerResultPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(InvitePlayerRequestPacket.PACKET_ID, InvitePlayerRequestPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(MarkBlockPacket.PACKET_ID, MarkBlockPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MarkEntityPacket.PACKET_ID, MarkEntityPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RemoveMarkerPacket.PACKET_ID, RemoveMarkerPacket.PACKET_CODEC);
    }

    public static void sendPartyInfo(Party<ServerPartyMember> party) {
        for (ServerPartyMember partyMember : party.partyMembers()) {
            if (partyMember.isOffline()) {
                continue;
            }

            ServerNetworkSender.sendPartyInfo(partyMember.player(), party);
        }
    }

    public static void sendPartyInfo(ServerPlayerEntity player, Party<ServerPartyMember> party) {
        ServerPlayNetworking.send(player, PartyInfoPacket.fromParty(party));
    }

    public static void removePartyInfo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new RemovePartyInfoPacket(PacketByteBufs.create()));
    }

    public static void sendPlayerInviteResponse(ServerPlayerEntity player, boolean isSuccessful) {
        ServerPlayNetworking.send(player,
                isSuccessful ? InvitePlayerResultPacket.successful() : InvitePlayerResultPacket.unsuccessful()
        );
    }

    public static void sendPlayerInvitation(ServerPlayerEntity player, Party<ServerPartyMember> party) {
        ServerPlayNetworking.send(player, InvitePlayerInvitationPacket.create(party));
    }

    public static void sendBlockMarker(ServerPlayerEntity player, ServerPlayerEntity markerPlayer, BlockPos blockPos) {
        ServerPlayNetworking.send(player, MarkBlockPacket.create(markerPlayer, blockPos));
    }

    public static void sendEntityMarker(ServerPlayerEntity player, ServerPlayerEntity markerPlayer, Entity entity) {
        ServerPlayNetworking.send(player, MarkEntityPacket.create(markerPlayer, entity));
    }

    public static void removeMarker(ServerPlayerEntity player, ServerPlayerEntity markerPlayer) {
        ServerPlayNetworking.send(player, RemoveMarkerPacket.create(markerPlayer));
    }

    public static void sendMessageToPlayer(ServerPlayerEntity player, Text message) {
        player.sendMessage(message);
    }

    public static void sendMessageToParty(Party<ServerPartyMember> party, Text message) {
        for (ServerPartyMember partyMember : party.partyMembers()) {
            partyMember.player().sendMessage(message, false);
        }
    }
}
