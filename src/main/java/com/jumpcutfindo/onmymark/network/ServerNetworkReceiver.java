package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.network.packets.CreatePartyPacket;
import com.jumpcutfindo.onmymark.network.packets.InviteToPartyPacket;
import com.jumpcutfindo.onmymark.network.packets.LeavePartyPacket;
import com.jumpcutfindo.onmymark.network.packets.PartyInvitationDecisionPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.*;
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
        this.onPartyInvitationDecision();
    }

    private void onCreateParty() {
        this.registerAndHandle(CreatePartyPacket.PACKET_ID, CreatePartyPacket.PACKET_CODEC, (payload, context) -> {
            Party party = OnMyMarkMod.PARTY_MANAGER.createParty(context.player(), payload.partyName());

            sendMessageToParty(party, Text.translatable("onmymark.action.onCreateParty", party.partyName()));
            syncPartyInfo(party);
        });
    }

    private void onLeaveParty() {
        this.registerAndHandle(LeavePartyPacket.PACKET_ID, LeavePartyPacket.PACKET_CODEC, (payload, context) -> {
            Party party = OnMyMarkMod.PARTY_MANAGER.leaveParty(context.player());

            sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.onLeaveParty.self"));
            removePartyInfo(context.player());
            if (party == null) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.onLeaveParty.disbanded"));
            } else {
                sendMessageToParty(party, Text.translatable("onmymark.action.onLeaveParty.other", context.player().getName()));
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
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.playerNotFound"));
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
                return;
            }

            try {
                Party party = OnMyMarkMod.PARTY_MANAGER.getPartyOfPlayer(context.player());
                OnMyMarkMod.PARTY_MANAGER.createInvite(party.partyId(), context.player(), invitee);

                // Inform the requester of the result
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), true);
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.onInviteToParty.successful", invitee.getName().getLiteralString()));

                // Inform invitee of invitation
                ServerNetworkSender.sendInvitationRequest(invitee, party);
                sendMessageToPlayer(invitee, Text.translatable("onmymark.action.onInviteToParty.gotInvited", context.player().getName()));
            } catch (PartyNotFoundException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
            } catch (InvalidPartyPermissionsException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidPermissions"));
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
            } catch (ExistingInviteException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.existingInvite"));
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
            } catch (AlreadyInPartyException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.alreadyInParty"));
                ServerNetworkSender.sendInviteToPartyResponse(context.player(), false);
            }
        });
    }

    private void onPartyInvitationDecision() {
        this.registerAndHandle(PartyInvitationDecisionPacket.PACKET_ID, PartyInvitationDecisionPacket.PACKET_CODEC, (payload, context) -> {
            ServerPlayerEntity invitedPlayer = context.player();

            try {
                if (payload.isAccept()) {
                    Party party = OnMyMarkMod.PARTY_MANAGER.acceptInvite(invitedPlayer);

                    // Broadcast to entire party that player has joined
                    sendMessageToParty(party, Text.translatable("onmymark.action.onInviteToParty.accepted", invitedPlayer.getName()));

                    // Send update to all members
                    syncPartyInfo(party);
                } else {
                    Party party = OnMyMarkMod.PARTY_MANAGER.rejectInvite(context.player());

                    // Inform leader that player has not joined
                    sendMessageToPlayer((ServerPlayerEntity) party.partyLeader().player(), Text.translatable("onmymark.action.onInviteToParty.rejected", invitedPlayer.getName()));
                }
            } catch (PartyInviteNotFoundException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidInvite"));
            } catch (PartyNotFoundException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.partyNotFound"));
            } catch (PlayerAlreadyInPartyException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.alreadyInParty"));
            } catch (InvalidPartyPermissionsException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidPermissions"));
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

    private void sendMessageToPlayer(ServerPlayerEntity player, Text message) {
        player.sendMessage(message);
    }

    private void sendMessageToParty(Party party, Text message) {
        for (PartyMember partyMember : party.partyMembers()) {
            partyMember.player().sendMessage(message, false);
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
