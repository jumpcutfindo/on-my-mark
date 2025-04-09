package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.graphics.OnMyMarkRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class OnMyMarkClientMod implements ClientModInitializer {
    public static OnMyMarkClientMod INSTANCE;

    private InputListener inputListener;
    private OnMyMarkRenderer renderer;

    private ClientPartyManager clientPartyManager;
    private ClientMarkerManager clientMarkerManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        this.clientPartyManager = new ClientPartyManager();
        this.clientMarkerManager = new ClientMarkerManager();


        this.renderer = new OnMyMarkRenderer(MinecraftClient.getInstance(), clientMarkerManager);
        this.inputListener = new InputListener(clientPartyManager, clientMarkerManager, renderer);

        this.registerRenderer();
        this.registerInputListener();
        this.registerClientCleanUp();
    }

    private void registerRenderer() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer ->
                layeredDrawer.attachLayerAfter(
                        IdentifiedLayer.MISC_OVERLAYS,
                        Identifier.of(OnMyMarkMod.MOD_ID, "render_overlay"),
                        renderer::render
                )
        );
    }

    private void registerInputListener() {
        ClientTickEvents.END_CLIENT_TICK.register(inputListener::onInput);
    }

    private void registerClientCleanUp() {
        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            this.clientMarkerManager.reset();
            this.clientPartyManager.reset();
        });
    }

    public ClientMarkerManager clientMarkerManager() {
        return clientMarkerManager;
    }

    public ClientPartyManager clientPartyManager() {
        return clientPartyManager;
    }
}
