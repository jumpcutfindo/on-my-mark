package com.jumpcutfindo.onmymark.client.network;

import com.jumpcutfindo.onmymark.network.packets.serverbound.*;
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

        ClientPlayNetworking.send(new CreatePartyC2SPacket(buffer));
    }

    public static void leaveParty() {
        ClientPlayNetworking.send(new LeavePartyC2SPacket(PacketByteBufs.create()));
    }

    public static void inviteToParty(String playerName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(playerName);

        ClientPlayNetworking.send(new InvitePlayerRequestC2SPacket(buf));
    }

    public static void kickFromParty(UUID playerId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(playerId);

        ClientPlayNetworking.send(new KickPlayerC2SPacket(buf));
    }

    public static void sendInvitationResponse(boolean isAccept) {
        ClientPlayNetworking.send(InvitePlayerDecisionC2SPacket.create(isAccept));
    }

    public static void markBlock(PlayerEntity player, BlockPos blockPos) {
        ClientPlayNetworking.send(MarkBlockC2SPacket.create(player, blockPos));
    }

    public static void markEntity(PlayerEntity player, Entity entity) {
        ClientPlayNetworking.send(MarkEntityC2SPacket.create(player, entity));
    }

    public static void removeMarker(PlayerEntity player) {
        ClientPlayNetworking.send(RemoveMarkerC2SPacket.create(player));
    }
}
