package com.jumpcutfindo.onmymark.client.input;

import com.jumpcutfindo.onmymark.client.graphics.OnMyMarkRenderer;
import com.jumpcutfindo.onmymark.client.graphics.markers.MarkerRenderer;
import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import com.jumpcutfindo.onmymark.marker.Marker;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveMarkerInputHandler extends InputHandler {
    private final OnMyMarkRenderer renderer;
    private final ClientMarkerManager clientMarkerManager;
    private final ClientPartyManager clientPartyManager;

    public RemoveMarkerInputHandler(MinecraftClient client, ClientMarkerManager clientMarkerManager, ClientPartyManager clientPartyManager, OnMyMarkRenderer renderer) {
        super(client);

        this.clientMarkerManager = clientMarkerManager;
        this.clientPartyManager = clientPartyManager;
        this.renderer = renderer;
    }

    @Override
    public int maxConcurrentInputs() {
        return 1;
    }

    @Override
    public long inputDelayMs() {
        return 0;
    }

    @Override
    public boolean execute() {
        super.execute();

        // Attempt to remove existing markers first, if any
        List<Marker> markers = findMarkersAroundCrosshair(client)
                .stream()
                .filter(marker -> marker.isOwner(client.player))
                .toList();

        if (!markers.isEmpty()) {
            // Since there are some markers around, we remove them and stop further processing
            clientMarkerManager.removeMarkerOf(this.clientPartyManager.self());
            ClientNetworkSender.removeMarker(client.player);
            return true;
        }

        return false;
    }


    /**
     * Retrieves the list of markers that are present around a small radius of the crosshair.
     */
    private List<Marker> findMarkersAroundCrosshair(MinecraftClient client) {
        int radius = 8;
        int centerX = client.getWindow().getScaledWidth() / 2;
        int centerY = client.getWindow().getScaledHeight() / 2;

        Map<Marker, MarkerRenderer> markerRendererMap = renderer.markerRendererMap();
        List<Marker> result = new ArrayList<>();

        for (Marker marker : markerRendererMap.keySet()) {
            MarkerRenderer mr = markerRendererMap.get(marker);
            Vector4f mrScreenPos = mr.screenPos();

            // Compute squared distance to avoid sqrt
            float dx = mrScreenPos.x - centerX;
            float dy = mrScreenPos.y - centerY;
            float distanceSq = dx * dx + dy * dy;

            if (distanceSq <= radius * radius) {
                result.add(marker);
            }
        }

        return result;
    }
}
