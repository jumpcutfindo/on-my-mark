package com.jumpcutfindo.onmymark.server.network;

import com.jumpcutfindo.onmymark.server.OnMyMarkServer;
import com.jumpcutfindo.onmymark.network.packets.serverbound.*;
import com.jumpcutfindo.onmymark.server.network.handlers.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ServerNetworkReceiver implements ModInitializer {
    private final CreatePartyC2SHandler createPartyHandler = new CreatePartyC2SHandler();
    private final LeavePartyC2SHandler leavePartyHandler = new LeavePartyC2SHandler();
    private final KickPlayerC2SHandler kickPlayerHandler = new KickPlayerC2SHandler();
    private final UpdateColorC2SHandler updateColorHandler = new UpdateColorC2SHandler();

    private final InvitePlayerRequestC2SHandler invitePlayerRequestHandler = new InvitePlayerRequestC2SHandler();
    private final InvitePlayerDecisionC2SHandler invitePlayerDecisionHandler = new InvitePlayerDecisionC2SHandler();

    private final MarkEntityC2SHandler markEntityHandler = new MarkEntityC2SHandler();
    private final MarkBlockC2SHandler markBlockHandler = new MarkBlockC2SHandler();
    private final PlayerReportC2SHandler playerReportHandler = new PlayerReportC2SHandler();
    private final RemoveMarkerC2SHandler removeMarkerHandler = new RemoveMarkerC2SHandler();

    @Override
    public void onInitialize() {
        // Register all receivers
        register(CreatePartyC2SPacket.PACKET_ID, CreatePartyC2SPacket.PACKET_CODEC, createPartyHandler);
        register(LeavePartyC2SPacket.PACKET_ID, LeavePartyC2SPacket.PACKET_CODEC, leavePartyHandler);
        register(KickPlayerC2SPacket.PACKET_ID, KickPlayerC2SPacket.PACKET_CODEC, kickPlayerHandler);
        register(UpdateColorC2SPacket.PACKET_ID, UpdateColorC2SPacket.PACKET_CODEC, updateColorHandler);

        register(InvitePlayerRequestC2SPacket.PACKET_ID, InvitePlayerRequestC2SPacket.PACKET_CODEC, invitePlayerRequestHandler);
        register(InvitePlayerDecisionC2SPacket.PACKET_ID, InvitePlayerDecisionC2SPacket.PACKET_CODEC, invitePlayerDecisionHandler);

        register(MarkEntityC2SPacket.PACKET_ID, MarkEntityC2SPacket.PACKET_CODEC, markEntityHandler);
        register(MarkBlockC2SPacket.PACKET_ID, MarkBlockC2SPacket.PACKET_CODEC, markBlockHandler);
        register(PlayerReportC2SPacket.PACKET_ID, PlayerReportC2SPacket.PACKET_CODEC, playerReportHandler);
        register(RemoveMarkerC2SPacket.PACKET_ID, RemoveMarkerC2SPacket.PACKET_CODEC, removeMarkerHandler);
    }

    private <T extends CustomPayload> void register(
            CustomPayload.Id<T> payloadId,
            PacketCodec<? super RegistryByteBuf, T> codec,
            ServerPacketHandler<T> handler
    ) {
        PayloadTypeRegistry.playC2S().register(payloadId, codec);
        ServerPlayNetworking.registerGlobalReceiver(payloadId, ((payload, context) -> {
            handler.handle(payload, new ServerPacketContext(context, OnMyMarkServer.INSTANCE.serverPartyManager(), OnMyMarkServer.INSTANCE.serverMarkerManager()));
        }));
    }
}
