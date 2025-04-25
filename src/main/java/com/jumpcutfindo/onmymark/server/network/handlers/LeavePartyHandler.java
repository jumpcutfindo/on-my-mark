package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.packets.LeavePartyPacket;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.minecraft.text.Text;

/**
 * Handler for when a party member attempts to leave the party they're in
 */
public class LeavePartyHandler implements ServerPacketHandler<LeavePartyPacket> {
    @Override
    public void handle(LeavePartyPacket payload, ServerPacketContext context) {
        ServerPartyManager partyManager = context.partyManager();

        Party<ServerPartyMember> party = partyManager.leaveParty(context.player());

        // Remove player from party
        ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.onLeaveParty.self"));
        ServerNetworkSender.removePartyInfo(context.player());

        ServerNetworkSender.sendMessageToParty(party, Text.translatable("onmymark.action.onLeaveParty.other", context.player().getName()));

        if (party.state() == Party.State.DISBANDED) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.onLeaveParty.disbanded"));
            ServerNetworkSender.sendMessageToParty(party, Text.translatable("onmymark.action.onLeaveParty.disbanded"));

            for (ServerPartyMember partyMember : party.partyMembers()) {
                ServerNetworkSender.removePartyInfo(partyMember.player());
            }
        } else {
            ServerNetworkSender.sendPartyInfo(party);
        }
    }
}
