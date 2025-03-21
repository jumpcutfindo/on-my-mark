package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.marker.Marker;

import java.util.ArrayList;
import java.util.List;

public class ClientMarkerManager {
    private List<Marker> markers;

    public ClientMarkerManager() {
        this.markers = new ArrayList<>();
    }

    public List<Marker> markers() {
        return markers;
    }

    public void addMarker(Marker marker) {
        this.markers.add(marker);
    }

    public void removeMarker(Marker marker) {
        this.markers.remove(marker);
    }
}
