package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.network.packets.CreatePartyPacket;
import com.jumpcutfindo.onmymark.network.packets.LeavePartyPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ServerNetworkReceiver implements ModInitializer {
    @Override
    public void onInitialize() {
        // Register all receivers
        this.onCreateParty();
        this.onLeaveParty();
    }

    private void onCreateParty() {
        this.registerAndHandle(CreatePartyPacket.PACKET_ID, CreatePartyPacket.PACKET_CODEC, (payload, context) -> {
            Party party = OnMyMarkMod.PARTY_MANAGER.createParty(context.player(), payload.partyName());

            sendMessageToParty(party, String.format("Created party with name \"%s\"", party.partyName()));
            syncPartyInfo(party);
        });
    }

    private void onLeaveParty() {
        this.registerAndHandle(LeavePartyPacket.PACKET_ID, LeavePartyPacket.PACKET_CODEC, (payload, context) -> {
            Party party = OnMyMarkMod.PARTY_MANAGER.leaveParty(context.player());

            sendMessageToPlayer(context.player(), "You have left the party.");
            removePartyInfo(context.player());
            if (party == null) {
                sendMessageToPlayer(context.player(), "As the last player left the party, the party has been disbanded.");
            } else {
                syncPartyInfo(party);
            }
        });
    }

    private void syncPartyInfo(Party party) {
        for (PartyMember partyMember : party.partyMembers()) {
            ServerNetworkSender.sendPartyInfo((ServerPlayerEntity) partyMember.player(), party);
        }
    }

    private void removePartyInfo(ServerPlayerEntity player) {
        ServerNetworkSender.removePartyInfo(player);
    }

    private void sendMessageToPlayer(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.literal(message));
    }

    private void sendMessageToParty(Party party, String message) {
        for (PartyMember partyMember : party.partyMembers()) {
            partyMember.player().sendMessage(Text.literal(message), false);
        }
    }

    /**
     * Helper function to help register and handle a packet received from the client
     * @param payloadId ID of the payload
     * @param payloadCodec Codec of the payload
     * @param handler How to handle the payload
     */
    private <T extends CustomPayload> void registerAndHandle(
            CustomPayload.Id<T> payloadId,
            PacketCodec<? super RegistryByteBuf, T> payloadCodec,
            ServerPlayNetworking.PlayPayloadHandler<T> handler
    ) {
        PayloadTypeRegistry.playC2S().register(payloadId, payloadCodec);
        ServerPlayNetworking.registerGlobalReceiver(payloadId, handler);
    }
}
