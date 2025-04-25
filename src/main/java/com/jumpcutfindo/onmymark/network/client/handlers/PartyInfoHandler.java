package com.jumpcutfindo.onmymark.network.client.handlers;

import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.network.client.ClientPacketContext;
import com.jumpcutfindo.onmymark.network.client.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.PartyInfoPacket;
import com.jumpcutfindo.onmymark.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;

public class PartyInfoHandler implements ClientPacketHandler<PartyInfoPacket> {
    @Override
    public void handle(PartyInfoPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        Party<ClientPartyMember> party = payload.toParty();

        partyManager.setParty(party);

        // Update screen if the player is looking
        if (context.client().currentScreen instanceof PartyScreen partyScreen) {
            partyScreen.setParty(party);
        }
    }
}
