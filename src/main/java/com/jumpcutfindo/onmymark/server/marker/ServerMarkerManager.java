package com.jumpcutfindo.onmymark.server.marker;

import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.server.marker.exceptions.UnhandledMarkerException;
import com.jumpcutfindo.onmymark.server.network.ServerNetworkSender;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerMarkerManager {
    private Map<Marker, Party<ServerPartyMember>> markerPartyMap;

    private Map<BlockPos, BlockMarker> blockPosMarkerMap;
    private Map<UUID, EntityMarker> entityMarkerMap;

    public ServerMarkerManager() {
        markerPartyMap = new HashMap<>();
        blockPosMarkerMap = new HashMap<>();
        entityMarkerMap = new HashMap<>();
    }

    public void addMarker(Party<ServerPartyMember> party, Marker marker) throws UnhandledMarkerException {
        markerPartyMap.put(marker, party);

        if (marker instanceof BlockMarker blockMarker) {
            blockPosMarkerMap.put(blockMarker.blockPos(), blockMarker);
        } else if (marker instanceof EntityMarker entityMarker) {
            entityMarkerMap.put(entityMarker.entityId(), entityMarker);
        } else {
            throw new UnhandledMarkerException();
        }
    }

    public void removeBlockMarker(BlockPos blockPos) {
        if (!blockPosMarkerMap.containsKey(blockPos)) {
            return;
        }

        BlockMarker blockMarker = blockPosMarkerMap.remove(blockPos);
        Party<ServerPartyMember> party = markerPartyMap.remove(blockMarker);

        ServerNetworkSender.removeMarker(party, ((ServerPartyMember) blockMarker.owner()).player());
    }

    public void removeEntityMarker(Entity entity) {
        if (!entityMarkerMap.containsKey(entity.getUuid())) {
            return;
        }

        EntityMarker entityMarker = entityMarkerMap.remove(entity.getUuid());
        Party<ServerPartyMember> party = markerPartyMap.remove(entityMarker);

        ServerNetworkSender.removeMarker(party, ((ServerPartyMember) entityMarker.owner()).player());
    }
}
