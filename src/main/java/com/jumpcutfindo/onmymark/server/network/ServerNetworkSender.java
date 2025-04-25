package com.jumpcutfindo.onmymark.server.network;

import com.jumpcutfindo.onmymark.network.packets.clientbound.*;
import com.jumpcutfindo.onmymark.network.packets.serverbound.InvitePlayerRequestC2SPacket;
import com.jumpcutfindo.onmymark.network.packets.serverbound.MarkEntityC2SPacket;
import com.jumpcutfindo.onmymark.network.packets.serverbound.RemoveMarkerC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ServerNetworkSender implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(PartyInfoS2CPacket.PACKET_ID, PartyInfoS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RemovePartyInfoS2CPacket.PACKET_ID, RemovePartyInfoS2CPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(InvitePlayerInvitationS2CPacket.PACKET_ID, InvitePlayerInvitationS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(InvitePlayerResultS2CPacket.PACKET_ID, InvitePlayerResultS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(InvitePlayerRequestC2SPacket.PACKET_ID, InvitePlayerRequestC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(MarkBlockResultS2CPacket.PACKET_ID, MarkBlockResultS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MarkEntityC2SPacket.PACKET_ID, MarkEntityC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RemoveMarkerC2SPacket.PACKET_ID, RemoveMarkerC2SPacket.PACKET_CODEC);
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
        ServerPlayNetworking.send(player, PartyInfoS2CPacket.fromParty(party));
    }

    public static void removePartyInfo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new RemovePartyInfoS2CPacket(PacketByteBufs.create()));
    }

    public static void sendPlayerInviteResponse(ServerPlayerEntity player, boolean isSuccessful) {
        ServerPlayNetworking.send(player,
                isSuccessful ? InvitePlayerResultS2CPacket.successful() : InvitePlayerResultS2CPacket.unsuccessful()
        );
    }

    public static void sendPlayerInvitation(ServerPlayerEntity player, Party<ServerPartyMember> party) {
        ServerPlayNetworking.send(player, InvitePlayerInvitationS2CPacket.create(party));
    }

    public static void sendBlockMarker(ServerPlayerEntity player, ServerPartyMember markerMember, BlockPos blockPos, BlockState blockState) {
        ServerPlayNetworking.send(player, MarkBlockResultS2CPacket.create(markerMember, blockPos, blockState));
    }

    public static void sendEntityMarker(ServerPlayerEntity player, ServerPlayerEntity markerPlayer, Entity entity) {
        ServerPlayNetworking.send(player, MarkEntityC2SPacket.create(markerPlayer, entity));
    }

    public static void removeMarker(ServerPlayerEntity player, ServerPlayerEntity markerPlayer) {
        ServerPlayNetworking.send(player, RemoveMarkerC2SPacket.create(markerPlayer));
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
