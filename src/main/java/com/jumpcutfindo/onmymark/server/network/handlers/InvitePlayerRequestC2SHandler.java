package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.packets.serverbound.InvitePlayerRequestC2SPacket;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.AlreadyInPartyException;
import com.jumpcutfindo.onmymark.server.party.exceptions.ExistingInviteException;
import com.jumpcutfindo.onmymark.server.party.exceptions.InvalidPartyPermissionsException;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.server.utils.ServerEntityUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Handler for when a party leader requests for a specified player to be invited to a party
 */
public class InvitePlayerRequestC2SHandler implements ServerPacketHandler<InvitePlayerRequestC2SPacket> {
    @Override
    public void handle(InvitePlayerRequestC2SPacket payload, ServerPacketContext context) {
        // Find the player if they exist in any world
        ServerPlayerEntity invitee = ServerEntityUtils.getPlayerByName(context, payload.playerName());

        if (invitee == null) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.playerNotFound"));
            ServerNetworkSender.sendPlayerInviteResponse(context.player(), false);
            return;
        }

        ServerPartyManager serverPartyManager = context.partyManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            serverPartyManager.createInvite(party.partyId(), context.player(), invitee);

            // Inform the requester of the result
            ServerNetworkSender.sendPlayerInviteResponse(context.player(), true);
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.onInviteToParty.successful", invitee.getName().getLiteralString()));

            // Inform invitee of invitation
            ServerNetworkSender.sendPlayerInvitation(invitee, party);
            ServerNetworkSender.sendMessageToPlayer(invitee, Text.translatable("onmymark.action.onInviteToParty.gotInvited", context.player().getName()));
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
            ServerNetworkSender.sendPlayerInviteResponse(context.player(), false);
        } catch (InvalidPartyPermissionsException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidPermissions"));
            ServerNetworkSender.sendPlayerInviteResponse(context.player(), false);
        } catch (ExistingInviteException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.existingInvite"));
            ServerNetworkSender.sendPlayerInviteResponse(context.player(), false);
        } catch (AlreadyInPartyException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.alreadyInParty"));
            ServerNetworkSender.sendPlayerInviteResponse(context.player(), false);
        }
    }
}
