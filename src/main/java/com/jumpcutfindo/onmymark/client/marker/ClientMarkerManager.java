package com.jumpcutfindo.onmymark.client.marker;

import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.marker.PlayerMarker;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.client.sounds.SoundPlayer;
import net.minecraft.client.MinecraftClient;

import java.util.*;
import java.util.stream.Stream;

public class ClientMarkerManager {
    private Map<PartyMember, Marker> markerMap;
    private Map<PartyMember, PlayerMarker> playerMarkerMap;

    public ClientMarkerManager() {
        this.markerMap = new HashMap<>();
        this.playerMarkerMap = new HashMap<>();
    }

    public void reset() {
        this.markerMap = new HashMap<>();
        this.playerMarkerMap = new HashMap<>();
    }

    public Collection<Marker> markers() {
        return Stream.concat(markerMap.values().stream(), playerMarkerMap.values().stream()).toList();
    }

    public void setMarker(PartyMember partyMember, Marker marker) {
        if (marker instanceof PlayerMarker playerMarker) {
            // Separately handle player markers
            this.playerMarkerMap.put(partyMember, playerMarker);
        } else {
            this.markerMap.put(partyMember, marker);
        }

        // Don't play any sounds if the marker is not visible from the current player's perspective
        if (!marker.isVisible(MinecraftClient.getInstance().player)) {
            return;
        }

        if (marker instanceof PlayerMarker) {
            SoundPlayer.playPlayerReportSound(MinecraftClient.getInstance().getSoundManager());
            return;
        }

        // Normal marker
        this.markerMap.put(partyMember, marker);

        if (partyMember.id().equals(MinecraftClient.getInstance().player.getUuid())) {
            // Marker was placed by self
            SoundPlayer.playPlaceMarkerSound(MinecraftClient.getInstance().getSoundManager());
        } else {
            // Marker was placed by other player
            SoundPlayer.playOtherMarkerSound(MinecraftClient.getInstance().getSoundManager());
        }
    }

    public void removeMarkerOf(PartyMember partyMember) {
        this.markerMap.remove(partyMember);
    }
}
