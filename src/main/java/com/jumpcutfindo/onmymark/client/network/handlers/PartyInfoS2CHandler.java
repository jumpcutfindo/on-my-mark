package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.graphics.screen.party.PartyScreen;
import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.network.packets.clientbound.PartyInfoS2CPacket;
import com.jumpcutfindo.onmymark.party.Party;

import java.util.UUID;

public class PartyInfoS2CHandler implements ClientPacketHandler<PartyInfoS2CPacket> {
    @Override
    public void handle(PartyInfoS2CPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        Party<ClientPartyMember> newParty = payload.toParty();

        this.updateMarkerOwners(context, newParty);

        partyManager.setParty(newParty);

        // Update screen if the player is looking
        if (context.client().currentScreen instanceof PartyScreen partyScreen) {
            partyScreen.setParty(newParty);
        }
    }

    private void updateMarkerOwners(ClientPacketContext context, Party<ClientPartyMember> newParty) {
        // Update marker owners
        ClientMarkerManager markerManager = context.markerManager();

        for (Marker marker : markerManager.markers()) {
            UUID ownerId = marker.owner().id();

            ClientPartyMember newMember = newParty.getMemberWithId(ownerId);
            marker.setOwner(newMember);
        }
    }
}
