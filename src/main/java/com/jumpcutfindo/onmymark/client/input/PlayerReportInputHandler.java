package com.jumpcutfindo.onmymark.client.input;

import com.jumpcutfindo.onmymark.client.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class PlayerReportInputHandler extends InputHandler {
    private final ClientPartyManager clientPartyManager;

    public PlayerReportInputHandler(MinecraftClient client, ClientPartyManager clientPartyManager) {
        super(client);
        this.clientPartyManager = clientPartyManager;
    }

    @Override
    public int maxConcurrentInputs() {
        return 1;
    }

    @Override
    public long inputDelayMs() {
        return 2000L;
    }

    @Override
    public boolean execute() {
        super.execute();

        if (client == null || client.player == null) {
            return false;
        }

        if (!this.clientPartyManager.isInParty()) {
            client.player.sendMessage(Text.translatable("onmymark.action.playerReport.notInParty"), false);
            return false;
        }

        ClientNetworkSender.playerReport();
        return true;
    }
}
