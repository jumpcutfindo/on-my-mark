package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.InvitePlayerDecisionC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.InvalidPartyPermissionsException;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyInviteNotFoundException;
import com.jumpcutfindo.onmymark.server.party.exceptions.PartyNotFoundException;
import com.jumpcutfindo.onmymark.server.party.exceptions.PlayerAlreadyInPartyException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Handler for when a player submits a decision on whether to accept or reject a party invite
 */
public class InvitePlayerDecisionC2SHandler implements ServerPacketHandler<InvitePlayerDecisionC2SPacket> {
    @Override
    public void handle(InvitePlayerDecisionC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();
        ServerPlayerEntity invitedPlayer = context.player();

        try {
            if (payload.isAccept()) {
                Party<ServerPartyMember> party = serverPartyManager.acceptInvite(invitedPlayer);

                // Broadcast to entire party that player has joined
                ServerNetworkSender.sendMessageToParty(party, Text.translatable("text.action.onmymark.onInviteToParty.accepted", invitedPlayer.getName()));

                // Send update to all members
                ServerNetworkSender.sendPartyInfo(party);
            } else {
                Party<ServerPartyMember> party = serverPartyManager.rejectInvite(context.player());

                // Inform leader that player has not joined
                ServerPartyMember partyLeader = party.partyLeader();
                ServerNetworkSender.sendMessageToPlayer(partyLeader.player(), Text.translatable("text.action.onmymark.onInviteToParty.rejected", invitedPlayer.getName()));
            }
        } catch (PartyInviteNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.invalidInvite"));
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.partyNotFound"));
        } catch (PlayerAlreadyInPartyException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.alreadyInParty"));
        } catch (InvalidPartyPermissionsException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.invalidPermissions"));
        }
    }
}
