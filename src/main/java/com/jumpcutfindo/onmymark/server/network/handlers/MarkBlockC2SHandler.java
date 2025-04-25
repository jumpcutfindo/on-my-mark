package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.MarkBlockC2SPacket;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMemberFilters;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.PartyNotFoundException;
import net.minecraft.text.Text;

/**
 * Handler for when a party member is attempting to mark a block
 */
public class MarkBlockC2SHandler implements ServerPacketHandler<MarkBlockC2SPacket> {
    @Override
    public void handle(MarkBlockC2SPacket payload, ServerPacketContext context) {
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
