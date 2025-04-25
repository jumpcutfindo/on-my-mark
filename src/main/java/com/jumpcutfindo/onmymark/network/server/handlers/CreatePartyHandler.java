package com.jumpcutfindo.onmymark.network.server.handlers;

import com.jumpcutfindo.onmymark.network.server.ServerNetworkSender;
import com.jumpcutfindo.onmymark.network.packets.CreatePartyPacket;
import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import com.jumpcutfindo.onmymark.network.server.ServerPacketHandler;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.ServerPartyManager;
import com.jumpcutfindo.onmymark.party.ServerPartyMember;
import com.jumpcutfindo.onmymark.party.exceptions.AlreadyInPartyException;
import net.minecraft.text.Text;

public class CreatePartyHandler implements ServerPacketHandler<CreatePartyPacket> {
    @Override
    public void handle(CreatePartyPacket payload, ServerPacketContext context) {
        ServerPartyManager partyManager = context.partyManager();

        try {
            Party<ServerPartyMember> party = partyManager.createParty(context.player(), payload.partyName());

            ServerNetworkSender.sendMessageToParty(party, Text.translatable("onmymark.action.onCreateParty", party.partyName()));
            ServerNetworkSender.sendPartyInfo(party);
        } catch (AlreadyInPartyException e) {
            ServerNetworkSender.sendMessageToPlayer(context.player(), Text.translatable("onmymark.action.exception.alreadyInParty"));
        }
    }
}
