package com.jumpcutfindo.onmymark.client.network;

import com.jumpcutfindo.onmymark.client.OnMyMarkClient;
import com.jumpcutfindo.onmymark.client.network.handlers.*;
import com.jumpcutfindo.onmymark.network.packets.clientbound.*;
import com.jumpcutfindo.onmymark.network.packets.serverbound.RemoveMarkerC2SPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ClientNetworkReceiver implements ClientModInitializer {
    private final PartyInfoS2CHandler partyInfoHandler = new PartyInfoS2CHandler();
    private final RemovePartyInfoS2CHandler removePartyInfoHandler = new RemovePartyInfoS2CHandler();
    private final InvitePlayerInvitationS2CHandler invitePlayerInvitationHandler = new InvitePlayerInvitationS2CHandler();

    private final MarkBlockResultS2CHandler markBlockHandler = new MarkBlockResultS2CHandler();
    private final MarkEntityResultS2CHandler markEntityHandler = new MarkEntityResultS2CHandler();
    private final MarkPlayerResultS2CHandler markPlayerHandler = new MarkPlayerResultS2CHandler();
    private final RemoveMarkerS2CHandler removeMarkerHandler = new RemoveMarkerS2CHandler();

    @Override
    public void onInitializeClient() {
        register(PartyInfoS2CPacket.PACKET_ID, PartyInfoS2CPacket.PACKET_CODEC, partyInfoHandler);
        register(RemovePartyInfoS2CPacket.PACKET_ID, RemovePartyInfoS2CPacket.PACKET_CODEC, removePartyInfoHandler);

        register(InvitePlayerInvitationS2CPacket.PACKET_ID, InvitePlayerInvitationS2CPacket.PACKET_CODEC, invitePlayerInvitationHandler);

        register(MarkBlockResultS2CPacket.PACKET_ID, MarkBlockResultS2CPacket.PACKET_CODEC, markBlockHandler);
        register(MarkEntityResultS2CPacket.PACKET_ID, MarkEntityResultS2CPacket.PACKET_CODEC, markEntityHandler);
        register(MarkPlayerResultS2CPacket.PACKET_ID, MarkPlayerResultS2CPacket.PACKET_CODEC, markPlayerHandler);

        register(RemoveMarkerC2SPacket.PACKET_ID, RemoveMarkerC2SPacket.PACKET_CODEC, removeMarkerHandler);
    }

    private <T extends CustomPayload> void register(
            CustomPayload.Id<T> payloadId,
            PacketCodec<? super RegistryByteBuf, T> codec,
            ClientPacketHandler<T> handler
    ) {
        ClientPlayNetworking.registerGlobalReceiver(payloadId, ((payload, context) -> {
            handler.handle(payload, new ClientPacketContext(context, OnMyMarkClient.INSTANCE.clientMarkerManager(), OnMyMarkClient.INSTANCE.clientPartyManager()));
        }));
    }
}
