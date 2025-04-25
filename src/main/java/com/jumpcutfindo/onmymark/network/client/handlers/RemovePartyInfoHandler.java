package com.jumpcutfindo.onmymark.network.client.handlers;

import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.network.client.ClientPacketContext;
import com.jumpcutfindo.onmymark.network.client.ClientPacketHandler;
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
