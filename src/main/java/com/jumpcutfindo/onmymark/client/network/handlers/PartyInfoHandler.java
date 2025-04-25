package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.PartyInfoPacket;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
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
