package com.jumpcutfindo.onmymark.network.client.handlers;

import com.jumpcutfindo.onmymark.client.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.client.OnMyMarkClientMod;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.network.client.ClientPacketContext;
import com.jumpcutfindo.onmymark.network.client.ClientPacketHandler;
import com.jumpcutfindo.onmymark.network.packets.MarkEntityPacket;
import com.jumpcutfindo.onmymark.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.utils.EntityUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class MarkEntityHandler implements ClientPacketHandler<MarkEntityPacket> {
    @Override
    public void handle(MarkEntityPacket payload, ClientPacketContext context) {
        ClientWorld world = context.client().world;
        
        if (world == null) {
            return;
        }

        Entity entity = EntityUtils.getEntityByUuid(world, context.player().getPos(), payload.entityId());

        ClientPartyManager partyManager = OnMyMarkClientMod.INSTANCE.clientPartyManager();
        Party<ClientPartyMember> party = partyManager.party();

        if (party.hasMemberWithId(payload.playerId())){
            ClientMarkerManager markerManager = OnMyMarkClientMod.INSTANCE.clientMarkerManager();
            PartyMember partyMember = party.getMemberWithId(payload.playerId());

            markerManager.setMarker(partyMember, new EntityMarker(partyMember, entity));
        }
    }
}
