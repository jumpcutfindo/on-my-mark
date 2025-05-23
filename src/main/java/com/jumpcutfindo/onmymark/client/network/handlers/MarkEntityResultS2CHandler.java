package com.jumpcutfindo.onmymark.client.network.handlers;

import com.jumpcutfindo.onmymark.client.OnMyMarkClient;
import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.network.ClientPacketContext;
import com.jumpcutfindo.onmymark.client.network.ClientPacketHandler;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.network.packets.clientbound.MarkEntityResultS2CPacket;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.client.world.ClientWorld;

public class MarkEntityResultS2CHandler implements ClientPacketHandler<MarkEntityResultS2CPacket> {
    @Override
    public void handle(MarkEntityResultS2CPacket payload, ClientPacketContext context) {
        ClientWorld world = context.client().world;
        
        if (world == null) {
            return;
        }

        ClientPartyManager partyManager = OnMyMarkClient.INSTANCE.clientPartyManager();
        Party<ClientPartyMember> party = partyManager.party();

        if (party.hasMemberWithId(payload.playerId())){
            ClientMarkerManager markerManager = OnMyMarkClient.INSTANCE.clientMarkerManager();
            PartyMember partyMember = party.getMemberWithId(payload.playerId());

            markerManager.setMarker(partyMember, new EntityMarker(partyMember, payload.worldRegistryKey(), payload.entityId(), payload.entityName(), payload.lastPos()));
        }
    }
}
