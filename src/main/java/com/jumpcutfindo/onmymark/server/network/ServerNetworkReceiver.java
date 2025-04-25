package com.jumpcutfindo.onmymark.server.network;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.network.packets.*;
import com.jumpcutfindo.onmymark.server.network.handlers.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ServerNetworkReceiver implements ModInitializer {
    private final CreatePartyHandler createPartyHandler = new CreatePartyHandler();
    private final LeavePartyHandler leavePartyHandler = new LeavePartyHandler();
    private final KickPlayerHandler kickPlayerHandler = new KickPlayerHandler();
    private final InvitePlayerRequestHandler invitePlayerRequestHandler = new InvitePlayerRequestHandler();
    private final InvitePlayerDecisionHandler invitePlayerDecisionHandler = new InvitePlayerDecisionHandler();

    private final MarkEntityHandler markEntityHandler = new MarkEntityHandler();
    private final MarkBlockHandler markBlockHandler = new MarkBlockHandler();
    private final RemoveMarkerHandler removeMarkerHandler = new RemoveMarkerHandler();

    @Override
    public void onInitialize() {
        // Register all receivers
        register(CreatePartyPacket.PACKET_ID, CreatePartyPacket.PACKET_CODEC, createPartyHandler);
        register(LeavePartyPacket.PACKET_ID, LeavePartyPacket.PACKET_CODEC, leavePartyHandler);
        register(KickPlayerPacket.PACKET_ID, KickPlayerPacket.PACKET_CODEC, kickPlayerHandler);

        register(InvitePlayerRequestPacket.PACKET_ID, InvitePlayerRequestPacket.PACKET_CODEC, invitePlayerRequestHandler);
        register(InvitePlayerDecisionPacket.PACKET_ID, InvitePlayerDecisionPacket.PACKET_CODEC, invitePlayerDecisionHandler);

        register(MarkEntityPacket.PACKET_ID, MarkEntityPacket.PACKET_CODEC, markEntityHandler);
        register(MarkBlockPacket.PACKET_ID, MarkBlockPacket.PACKET_CODEC, markBlockHandler);
        register(RemoveMarkerPacket.PACKET_ID, RemoveMarkerPacket.PACKET_CODEC, removeMarkerHandler);
    }

    private <T extends CustomPayload> void register(
            CustomPayload.Id<T> payloadId,
            PacketCodec<? super RegistryByteBuf, T> codec,
            ServerPacketHandler<T> handler
    ) {
        PayloadTypeRegistry.playC2S().register(payloadId, codec);
        ServerPlayNetworking.registerGlobalReceiver(payloadId, ((payload, context) -> {
            handler.handle(payload, new ServerPacketContext(context, OnMyMarkMod.PARTY_MANAGER));
        }));
    }
}
