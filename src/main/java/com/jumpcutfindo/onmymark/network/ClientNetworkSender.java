package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.network.packets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class ClientNetworkSender implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }

    public static void createParty(String partyName) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeString(partyName);

        ClientPlayNetworking.send(new CreatePartyPacket(buffer));
    }

    public static void leaveParty() {
        ClientPlayNetworking.send(new LeavePartyPacket(PacketByteBufs.create()));
    }

    public static void inviteToParty(String playerName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(playerName);

        ClientPlayNetworking.send(new InviteToPartyPacket(buf));
    }

    public static void kickFromParty(UUID playerId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(playerId);

        ClientPlayNetworking.send(new KickFromPartyPacket(buf));
    }

    public static void sendInvitationResponse(boolean isAccept) {
        ClientPlayNetworking.send(PartyInvitationDecisionPacket.create(isAccept));
    }

    public static void markBlock(PlayerEntity player, BlockPos blockPos) {
        ClientPlayNetworking.send(MarkBlockPacket.create(player, blockPos));
    }

    public static void markEntity(PlayerEntity player, Entity entity) {
        ClientPlayNetworking.send(MarkEntityPacket.create(player, entity));
    }

    public static void removeMarker(PlayerEntity player) {
        ClientPlayNetworking.send(RemoveMarkerPacket.create(player));
    }
}
