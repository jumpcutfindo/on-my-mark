package com.jumpcutfindo.onmymark.network;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.network.packets.*;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.*;
import com.jumpcutfindo.onmymark.utils.EntityUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.UUID;

public class ServerNetworkReceiver implements ModInitializer {
    @Override
    public void onInitialize() {
        // Register all receivers
        this.onCreateParty();
        this.onLeaveParty();
        this.onInviteToParty();
        this.onPartyInvitationDecision();

        this.onKickPlayer();

        this.onMarkBlock();
        this.onMarkEntity();
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

            sendMessageToParty(party, Text.translatable("onmymark.action.onLeaveParty.other", context.player().getName()));

            if (party.state() == Party.State.DISBANDED) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.onLeaveParty.disbanded"));
                sendMessageToParty(party, Text.translatable("onmymark.action.onLeaveParty.disbanded"));

                for (PartyMember partyMember : party.partyMembers()) {
                    removePartyInfo((ServerPlayerEntity) partyMember.player());
                }
            } else {
                syncPartyInfo(party);
            }
        });
    }

    private void onKickPlayer() {
        this.registerAndHandle(KickFromPartyPacket.PACKET_ID, KickFromPartyPacket.PACKET_CODEC, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ServerPlayerEntity otherPlayer = this.getPlayerById(context, payload.playerId());

            if (otherPlayer == null) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.playerNotFound"));
                return;
            }

            try {
                Party party = OnMyMarkMod.PARTY_MANAGER.getPartyOfPlayer(player);
                OnMyMarkMod.PARTY_MANAGER.removePlayerFromParty(party.partyId(), player, otherPlayer);

                sendMessageToPlayer(otherPlayer, Text.translatable("onmymark.action.onKickFromParty.self"));
                removePartyInfo(otherPlayer);

                sendMessageToParty(party, Text.translatable("onmymark.action.onKickFromParty.other", otherPlayer.getName()));
            } catch (PartyNotFoundException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
            } catch (RemovePartyLeaderException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.removePartyLeader"));
            } catch (PlayerNotInPartyException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.playerNotFound"));
            } catch (InvalidPartyPermissionsException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidPermissions"));
            }
        });
    }

    private void onInviteToParty() {
        this.registerAndHandle(InviteToPartyPacket.PACKET_ID, InviteToPartyPacket.PACKET_CODEC, (payload, context) -> {
            // Find the player if they exist in any world
            ServerPlayerEntity invitee = getPlayerByName(context, payload.playerName());

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

    private void onMarkBlock() {
        this.registerAndHandle(MarkBlockPacket.PACKET_ID, MarkBlockPacket.PACKET_CODEC, (payload, context) -> {
            try {
                Party party = OnMyMarkMod.PARTY_MANAGER.getPartyOfPlayer(context.player());

                for (PartyMember partyMember : party.partyMembers()) {
                    ServerNetworkSender.sendBlockMarker((ServerPlayerEntity) partyMember.player(), context.player(), payload.blockPos());
                }
            } catch (PartyNotFoundException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
            }
        });
    }

    private void onMarkEntity() {
        this.registerAndHandle(MarkEntityPacket.PACKET_ID, MarkEntityPacket.PACKET_CODEC, ((payload, context) -> {
            try {
                Party party = OnMyMarkMod.PARTY_MANAGER.getPartyOfPlayer(context.player());
                ServerWorld world = context.player().getServerWorld();

                Entity entity = EntityUtils.getEntityByUuid(world, context.player().getPos(), payload.entityId());

                if (entity == null) {
                    return;
                }

                for (PartyMember partyMember : party.partyMembers()) {
                    ServerNetworkSender.sendEntityMarker((ServerPlayerEntity) partyMember.player(), context.player(), entity);
                }

            } catch (PartyNotFoundException e) {
                sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
            }
        }));
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

    private ServerPlayerEntity getPlayerById(ServerPlayNetworking.Context context, UUID playerId) {
        for (ServerWorld world : context.server().getWorlds()) {
            ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUuid(playerId);

            if (player != null) {
                return player;
            }
        }

        // Player not found
        return null;
    }

    private ServerPlayerEntity getPlayerByName(ServerPlayNetworking.Context context, String playerName) {
        for (ServerWorld world : context.server().getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.getName().getLiteralString().equals(playerName)) {
                    return player;
                }
            }
        }

        return null;
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
