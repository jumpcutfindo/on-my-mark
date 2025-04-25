package com.jumpcutfindo.onmymark.client.marker;

import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.client.sounds.SoundPlayer;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public class ClientMarkerManager {
    private Map<PartyMember, Marker> markerMap;

    public ClientMarkerManager() {
        this.markerMap = new HashMap<>();
    }

    public void reset() {
        this.markerMap = new HashMap<>();
    }

    public Collection<Marker> markers() {
        return markerMap.values();
    }

    public void setMarker(PartyMember partyMember, Marker marker) {
        if (partyMember.id().equals(MinecraftClient.getInstance().player.getUuid())) {
            // Marker was placed by self
            SoundPlayer.playPlaceMarkerSound(MinecraftClient.getInstance().getSoundManager());
        } else {
            // Marker was placed by other player
            SoundPlayer.playOtherMarkerSound(MinecraftClient.getInstance().getSoundManager());
        }

        this.markerMap.put(partyMember, marker);
    }

    public void removeMarkerOf(PartyMember partyMember) {
        this.markerMap.remove(partyMember);
    }
}
