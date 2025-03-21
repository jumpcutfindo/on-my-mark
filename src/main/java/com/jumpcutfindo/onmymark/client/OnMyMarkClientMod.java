package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.graphics.OnMyMarkRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class OnMyMarkClientMod implements ClientModInitializer {
    private ClientMarkerManager clientMarkerManager;
    private OnMyMarkRenderer renderer;

    @Override
    public void onInitializeClient() {
        this.clientMarkerManager = new ClientMarkerManager();
        this.renderer = new OnMyMarkRenderer(MinecraftClient.getInstance());

        this.registerRenderer();
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
}
