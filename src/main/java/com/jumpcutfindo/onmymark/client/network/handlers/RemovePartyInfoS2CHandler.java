package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.clientbound.RemovePartyInfoS2CPacket;

public class RemovePartyInfoS2CHandler implements ClientPacketHandler<RemovePartyInfoS2CPacket> {
    @Override
    public void handle(RemovePartyInfoS2CPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        partyManager.setParty(null);

        // Remove all markers since no party
        ClientMarkerManager markerManager = context.markerManager();
        markerManager.reset();

        // Update screen if the player is looking
        if (context.client().currentScreen instanceof PartyScreen partyScreen) {
            partyScreen.setParty(null);
        }
    }
}
