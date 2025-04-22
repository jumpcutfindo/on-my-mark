package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.sounds.SoundPlayer;
import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.party.PartyMember;
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
        SoundPlayer.playPlaceMarkerSound(MinecraftClient.getInstance().getSoundManager());
        this.markerMap.put(partyMember, marker);
    }

    public void removeMarkerOf(PartyMember partyMember) {
        this.markerMap.remove(partyMember);
    }
}
