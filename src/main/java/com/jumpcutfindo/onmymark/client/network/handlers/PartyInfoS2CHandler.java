package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.clientbound.PartyInfoS2CPacket;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;

public class PartyInfoS2CHandler implements ClientPacketHandler<PartyInfoS2CPacket> {
    @Override
    public void handle(PartyInfoS2CPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        Party<ClientPartyMember> party = payload.toParty();

        partyManager.setParty(party);

        // Update screen if the player is looking
        if (context.client().currentScreen instanceof PartyScreen partyScreen) {
            partyScreen.setParty(party);
        }
    }
}
