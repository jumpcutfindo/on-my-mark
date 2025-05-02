package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.marker.PlayerMarker;
import com.jumpcutfindo.onmymark.network.packets.clientbound.MarkPlayerResultS2CPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.client.world.ClientWorld;

public class MarkPlayerResultS2CHandler implements ClientPacketHandler<MarkPlayerResultS2CPacket> {
    @Override
    public void handle(MarkPlayerResultS2CPacket payload, ClientPacketContext context) {
        ClientWorld world = context.client().world;

        if (world == null) {
            return;
        }

        ClientPartyManager partyManager = context.partyManager();
        Party<ClientPartyMember> party = partyManager.party();

        if (party.hasMemberWithId(payload.playerId())){
            ClientMarkerManager markerManager = context.markerManager();
            PartyMember partyMember = party.getMemberWithId(payload.playerId());

            markerManager.setMarker(partyMember, new PlayerMarker(partyMember, payload.worldRegistryKey(), payload.markerPlayerId(), payload.markerPlayerName(), payload.lastPos()));
        }
    }
}
