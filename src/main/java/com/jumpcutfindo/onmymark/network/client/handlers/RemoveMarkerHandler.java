package com.jumpcutfindo.onmymark.network.client.handlers;

import com.jumpcutfindo.onmymark.client.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.network.client.ClientPacketContext;
import com.jumpcutfindo.onmymark.network.client.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.RemoveMarkerPacket;
import com.jumpcutfindo.onmymark.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;

public class RemoveMarkerHandler implements ClientPacketHandler<RemoveMarkerPacket> {
    @Override
    public void handle(RemoveMarkerPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        ClientMarkerManager markerManager = context.markerManager();

        Party<ClientPartyMember> party = partyManager.party();

        if (party.hasMemberWithId(payload.markerPlayerId())) {
            PartyMember partyMember = party.getMemberWithId(payload.markerPlayerId());

            markerManager.removeMarkerOf(partyMember);
        }
    }
}
