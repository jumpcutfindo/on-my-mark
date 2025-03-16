package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.network.packets.CreatePartyPacket;
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
}
