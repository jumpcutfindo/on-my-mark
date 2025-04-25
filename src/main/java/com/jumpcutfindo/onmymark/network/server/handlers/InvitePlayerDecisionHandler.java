package com.jumpcutfindo.onmymark.network.server.handlers;

import com.jumpcutfindo.onmymark.network.server.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.packets.InvitePlayerDecisionPacket;
import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import com.jumpcutfindo.onmymark.network.server.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.InvalidPartyPermissionsException;
import com.jumpcutfindo.onmymark.party.exceptions.PartyInviteNotFoundException;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.party.exceptions.PlayerAlreadyInPartyException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Handler for when a player submits a decision on whether to accept or reject a party invite
 */
public class InvitePlayerDecisionHandler implements ServerPacketHandler<InvitePlayerDecisionPacket> {
    @Override
    public void handle(InvitePlayerDecisionPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();
        ServerPlayerEntity invitedPlayer = context.player();

        try {
            if (payload.isAccept()) {
                Party<ServerPartyMember> party = serverPartyManager.acceptInvite(invitedPlayer);

                // Broadcast to entire party that player has joined
                ServerNetworkSender.sendMessageToParty(party, Text.translatable("onmymark.action.onInviteToParty.accepted", invitedPlayer.getName()));

                // Send update to all members
                ServerNetworkSender.sendPartyInfo(party);
            } else {
                Party<ServerPartyMember> party = serverPartyManager.rejectInvite(context.player());

                // Inform leader that player has not joined
                ServerPartyMember partyLeader = party.partyLeader();
                ServerNetworkSender.sendMessageToPlayer(partyLeader.player(), Text.translatable("onmymark.action.onInviteToParty.rejected", invitedPlayer.getName()));
            }
        } catch (PartyInviteNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidInvite"));
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.partyNotFound"));
        } catch (PlayerAlreadyInPartyException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.alreadyInParty"));
        } catch (InvalidPartyPermissionsException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidPermissions"));
        }
    }
}
