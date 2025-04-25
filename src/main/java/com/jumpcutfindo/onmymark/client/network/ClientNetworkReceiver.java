package com.jumpcutfindo.onmymark.client.network;

import com.jumpcutfindo.onmymark.OnMyMarkClientMod;
import com.jumpcutfindo.onmymark.client.network.handlers.*;
import com.jumpcutfindo.onmymark.network.packets.serverbound.MarkBlockC2SPacket;
import com.jumpcutfindo.onmymark.network.packets.serverbound.MarkEntityC2SPacket;
import com.jumpcutfindo.onmymark.network.packets.serverbound.RemoveMarkerC2SPacket;
import com.jumpcutfindo.onmymark.network.packets.clientbound.InvitePlayerInvitationS2CPacket;
import com.jumpcutfindo.onmymark.network.packets.clientbound.PartyInfoS2CPacket;
import com.jumpcutfindo.onmymark.network.packets.clientbound.RemovePartyInfoS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ClientNetworkReceiver implements ClientModInitializer {
    private final PartyInfoS2CHandler partyInfoHandler = new PartyInfoS2CHandler();
    private final RemovePartyInfoS2CHandler removePartyInfoHandler = new RemovePartyInfoS2CHandler();
    private final InvitePlayerInvitationS2CHandler invitePlayerInvitationHandler = new InvitePlayerInvitationS2CHandler();

    private final MarkBlockS2CHandler markBlockHandler = new MarkBlockS2CHandler();
    private final MarkEntityS2CHandler markEntityHandler = new MarkEntityS2CHandler();
    private final RemoveMarkerS2CHandler removeMarkerHandler = new RemoveMarkerS2CHandler();

    @Override
    public void onInitializeClient() {
        register(PartyInfoS2CPacket.PACKET_ID, PartyInfoS2CPacket.PACKET_CODEC, partyInfoHandler);
        register(RemovePartyInfoS2CPacket.PACKET_ID, RemovePartyInfoS2CPacket.PACKET_CODEC, removePartyInfoHandler);

        register(InvitePlayerInvitationS2CPacket.PACKET_ID, InvitePlayerInvitationS2CPacket.PACKET_CODEC, invitePlayerInvitationHandler);

        register(MarkBlockC2SPacket.PACKET_ID, MarkBlockC2SPacket.PACKET_CODEC, markBlockHandler);
        register(MarkEntityC2SPacket.PACKET_ID, MarkEntityC2SPacket.PACKET_CODEC, markEntityHandler);
        register(RemoveMarkerC2SPacket.PACKET_ID, RemoveMarkerC2SPacket.PACKET_CODEC, removeMarkerHandler);
    }

    private <T extends CustomPayload> void register(
            CustomPayload.Id<T> payloadId,
            PacketCodec<? super RegistryByteBuf, T> codec,
            ClientPacketHandler<T> handler
    ) {
        ClientPlayNetworking.registerGlobalReceiver(payloadId, ((payload, context) -> {
            // Avoid registering payloads here as the server has already registered them

            handler.handle(payload, new ClientPacketContext(context, OnMyMarkClientMod.INSTANCE.clientMarkerManager(), OnMyMarkClientMod.INSTANCE.clientPartyManager()));
        }));
    }
}
