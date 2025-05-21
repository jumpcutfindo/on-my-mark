package com.jumpcutfindo.onmymark.server.network.handlers;

import com.jumpcutfindo.onmymark.network.packets.serverbound.CreatePartyC2SPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import com.jumpcutfindo.onmymark.server.network.ServerPacketHandler;
import com.jumpcutfindo.onmymark.server.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.server.party.exceptions.AlreadyInPartyException;
import net.minecraft.text.Text;

/**
 * Handler for when a player attempts to create a party
 */
public class CreatePartyC2SHandler implements ServerPacketHandler<CreatePartyC2SPacket> {
    @Override
    public void handle(CreatePartyC2SPacket payload, ServerPacketContext context) {
        ServerPartyManager partyManager = context.partyManager();

        try {
            Party<ServerPartyMember> party = partyManager.createParty(context.player(), payload.partyName());

            ServerNetworkSender.sendMessageToParty(party, Text.translatable("text.action.onmymark.onCreateParty", party.partyName()));
            ServerNetworkSender.sendPartyInfo(party);
        } catch (AlreadyInPartyException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("text.action.onmymark.exception.alreadyInParty"));
        }
    }
}
