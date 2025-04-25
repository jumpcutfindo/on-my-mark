package com.jumpcutfindo.onmymark.network.server.handlers;

import com.jumpcutfindo.onmymark.network.packets.MarkBlockPacket;
import com.jumpcutfindo.onmymark.network.server.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import com.jumpcutfindo.onmymark.network.server.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMemberFilters;
import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import net.minecraft.text.Text;

public class MarkBlockHandler implements ServerPacketHandler<MarkBlockPacket> {
    @Override
    public void handle(MarkBlockPacket payload, ServerPacketContext context) {
        ServerPartyManager serverPartyManager = context.partyManager();

        try {
            Party<ServerPartyMember> party = serverPartyManager.getPartyOfPlayer(context.player());
            ServerPartyMember playerPartyMember = serverPartyManager.getOrCreatePlayer(context.player());

            // Send markers only to players in the same dimension
            PartyMemberFilters.SameDimensionFilter sameDimensionFilter = new PartyMemberFilters.SameDimensionFilter(playerPartyMember);
            for (ServerPartyMember partyMember : party.partyMembers(sameDimensionFilter)) {
                ServerNetworkSender.sendBlockMarker(partyMember.player(), context.player(), payload.blockPos());
            }
        } catch (PartyNotFoundException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.invalidParty"));
        }
    }
}
