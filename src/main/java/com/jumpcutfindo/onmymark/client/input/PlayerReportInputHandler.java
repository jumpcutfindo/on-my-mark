package com.jumpcutfindo.onmymark.client.input;

import com.jumpcutfindo.onmymark.client.graphics.OnMyMarkRenderer;
import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class PlayerReportInputHandler implements InputHandler {
    private final ClientPartyManager clientPartyManager;
    private final ClientMarkerManager clientMarkerManager;
    private final OnMyMarkRenderer renderer;

    public PlayerReportInputHandler(ClientPartyManager clientPartyManager, ClientMarkerManager clientMarkerManager, OnMyMarkRenderer renderer) {
        this.clientPartyManager = clientPartyManager;
        this.clientMarkerManager = clientMarkerManager;
        this.renderer = renderer;
    }

    @Override
    public void execute(MinecraftClient client) {
        if (client == null || client.player == null) {
            return;
        }

        if (!this.clientPartyManager.isInParty()) {
            client.player.sendMessage(Text.translatable("onmymark.action.playerReport.notInParty"), false);
            return;
        }

        ClientNetworkSender.playerReport();
    }
}
