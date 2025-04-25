package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.serverbound.RemoveMarkerC2SPacket;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;

public class RemoveMarkerS2CHandler implements ClientPacketHandler<RemoveMarkerC2SPacket> {
    @Override
    public void handle(RemoveMarkerC2SPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        ClientMarkerManager markerManager = context.markerManager();

        Party<ClientPartyMember> party = partyManager.party();

        if (party.hasMemberWithId(payload.markerPlayerId())) {
            PartyMember partyMember = party.getMemberWithId(payload.markerPlayerId());

            markerManager.removeMarkerOf(partyMember);
        }
    }
}
