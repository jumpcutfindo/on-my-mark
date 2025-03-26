package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.network.packets.CreatePartyPacket;
import com.jumpcutfindo.onmymark.network.packets.InviteToPartyPacket;
import com.jumpcutfindo.onmymark.network.packets.LeavePartyPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

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
}
