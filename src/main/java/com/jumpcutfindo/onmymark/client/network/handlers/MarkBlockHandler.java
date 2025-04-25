package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.MarkBlockPacket;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;

public class MarkBlockHandler implements ClientPacketHandler<MarkBlockPacket> {
    @Override
    public void handle(MarkBlockPacket payload, ClientPacketContext context) {
        ClientPartyManager partyManager = context.partyManager();
        ClientMarkerManager markerManager = context.markerManager();

        Party<ClientPartyMember> party = partyManager.party();

        if (party.hasMemberWithId(payload.playerId())){
            PartyMember partyMember = party.getMemberWithId(payload.playerId());

            markerManager.setMarker(partyMember, new BlockMarker(partyMember, payload.blockPos(), context.client().world.getBlockState(payload.blockPos())));
        }
    }
}
