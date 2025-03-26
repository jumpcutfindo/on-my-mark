package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.network.packets.CreatePartyPacket;
import com.jumpcutfindo.onmymark.network.packets.InviteToPartyPacket;
import com.jumpcutfindo.onmymark.network.packets.InviteToPartyResponsePacket;
import com.jumpcutfindo.onmymark.network.packets.LeavePartyPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.InvalidPartyPermissionsException;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class ServerNetworkReceiver implements ModInitializer {
    @Override
    public void onInitialize() {
        // Register all receivers
        this.onCreateParty();
        this.onLeaveParty();
        this.onInviteToParty();
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

    private void onInviteToParty() {
        this.registerAndHandle(InviteToPartyPacket.PACKET_ID, InviteToPartyPacket.PACKET_CODEC, (payload, context) -> {
            // Find the player if they exist in any world
            ServerPlayerEntity invitee = null;
            for (ServerWorld world : context.server().getWorlds()) {
                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (player.getName().getLiteralString().equals(payload.playerName())) {
                        invitee = player;
                        break;
                    }
                }

                if (invitee != null) break;
            }

            if (invitee == null) {
                sendMessageToPlayer(context.player(), "Unable to find the specified player");
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
                return;
            }

            try {
                Party party = OnMyMarkMod.PARTY_MANAGER.getPartyOfPlayer(context.player());
                OnMyMarkMod.PARTY_MANAGER.createInvite(party.partyId(), context.player(), invitee);

                // Inform the requester of the result
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), true);
                sendMessageToPlayer(context.player(), String.format("An invitation has been sent to %s", invitee.getName().getLiteralString()));

                // Inform invitee of invitation

            } catch (PartyNotFoundException e) {
                sendMessageToPlayer(context.player(), "Unable to perform that action as you are not in a party");
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
            } catch (InvalidPartyPermissionsException e) {
                sendMessageToPlayer(context.player(), "Unable to perform that action as you do not have valid permissions");
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
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
