package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.graphics.OnMyMarkRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class OnMyMarkClientMod implements ClientModInitializer {
    private InputListener inputListener;
    private OnMyMarkRenderer renderer;

    private ClientMarkerManager clientMarkerManager;

    @Override
    public void onInitializeClient() {
        this.renderer = new OnMyMarkRenderer(MinecraftClient.getInstance());
        this.inputListener = new InputListener();

        this.clientMarkerManager = new ClientMarkerManager();

        this.registerRenderer();
        this.registerInputListener();
    }

    private void registerRenderer() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer ->
                layeredDrawer.attachLayerAfter(
                        IdentifiedLayer.MISC_OVERLAYS,
                        Identifier.of(OnMyMarkMod.MOD_ID, "render_overlay"),
                        (drawContext, tickCounter) -> {
                            renderer.render(clientMarkerManager, drawContext, tickCounter);
                        }
                )
        );
    }

    private void registerInputListener() {
        ClientTickEvents.END_CLIENT_TICK.register(inputListener::onInput);
    }
}
