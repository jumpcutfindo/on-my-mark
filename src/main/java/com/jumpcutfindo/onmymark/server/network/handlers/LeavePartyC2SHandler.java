package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.LeavePartyC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.minecraft.text.Text;

/**
 * Handler for when a party member attempts to leave the party they're in
 */
public class LeavePartyC2SHandler implements ServerPacketHandler<LeavePartyC2SPacket> {
    @Override
    public void handle(LeavePartyC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager partyManager = context.partyManager();

        Party<ServerPartyMember> party = partyManager.leaveParty(context.player());

        // Remove player from party
        ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.onLeaveParty.self"));
        ServerNetworkSender.removePartyInfo(context.player());

        // Remove player's markers
        ServerNetworkSender.removeMarker(party, context.player());

        ServerNetworkSender.sendMessageToParty(party, Text.translatable("text.action.onmymark.onLeaveParty.other", context.player().getName()));

        if (party.state() == Party.State.DISBANDED) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.onLeaveParty.disbanded"));
            ServerNetworkSender.sendMessageToParty(party, Text.translatable("text.action.onmymark.onLeaveParty.disbanded"));

            for (ServerPartyMember partyMember : party.partyMembers()) {
                ServerNetworkSender.removePartyInfo(partyMember.player());
            }
        } else {
            ServerNetworkSender.sendPartyInfo(party);
        }
    }
}
