package com.jumpcutfindo.onmymark.network.server.handlers;

import com.jumpcutfindo.onmymark.network.server.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.packets.LeavePartyPacket;
import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import com.jumpcutfindo.onmymark.network.server.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import net.minecraft.text.Text;

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
