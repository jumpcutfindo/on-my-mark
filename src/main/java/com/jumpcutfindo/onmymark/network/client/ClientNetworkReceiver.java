package com.jumpcutfindo.onmymark.network.client;

import com.jumpcutfindo.onmymark.client.OnMyMarkClientMod;
import com.jumpcutfindo.onmymark.network.client.handlers.*;
import com.jumpcutfindo.onmymark.network.packets.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ClientNetworkReceiver implements ClientModInitializer {
    private final PartyInfoHandler partyInfoHandler = new PartyInfoHandler();
    private final RemovePartyInfoHandler removePartyInfoHandler = new RemovePartyInfoHandler();
    private final InvitePlayerInvitationHandler invitePlayerInvitationHandler = new InvitePlayerInvitationHandler();

    private final MarkBlockHandler markBlockHandler = new MarkBlockHandler();
    private final MarkEntityHandler markEntityHandler = new MarkEntityHandler();
    private final RemoveMarkerHandler removeMarkerHandler = new RemoveMarkerHandler();

    @Override
    public void onInitializeClient() {
        register(PartyInfoPacket.PACKET_ID, PartyInfoPacket.PACKET_CODEC, partyInfoHandler);
        register(RemovePartyInfoPacket.PACKET_ID, RemovePartyInfoPacket.PACKET_CODEC, removePartyInfoHandler);

        register(InvitePlayerInvitationPacket.PACKET_ID, InvitePlayerInvitationPacket.PACKET_CODEC, invitePlayerInvitationHandler);

        register(MarkBlockPacket.PACKET_ID, MarkBlockPacket.PACKET_CODEC, markBlockHandler);
        register(MarkEntityPacket.PACKET_ID, MarkEntityPacket.PACKET_CODEC, markEntityHandler);
        register(RemoveMarkerPacket.PACKET_ID, RemoveMarkerPacket.PACKET_CODEC, removeMarkerHandler);
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
