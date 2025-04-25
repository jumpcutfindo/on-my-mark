package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.RemovePartyInfoPacket;

public class RemovePartyInfoHandler implements ClientPacketHandler<RemovePartyInfoPacket> {
    @Override
    public void handle(RemovePartyInfoPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        partyManager.setParty(null);

        // Update screen if the player is looking
        if (context.client().currentScreen instanceof PartyScreen partyScreen) {
            partyScreen.setParty(null);
        }
    }
}
