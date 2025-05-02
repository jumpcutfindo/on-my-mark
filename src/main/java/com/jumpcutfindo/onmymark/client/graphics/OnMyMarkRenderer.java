package com.jumpcutfindo.onmymark.client.graphics;

import com.jumpcutfindo.onmymark.client.graphics.markers.BlockMarkerRenderer;
import com.jumpcutfindo.onmymark.client.graphics.markers.EntityMarkerRenderer;
import com.jumpcutfindo.onmymark.client.graphics.markers.MarkerRenderer;
import com.jumpcutfindo.onmymark.client.graphics.markers.PlayerMarkerRenderer;
import com.jumpcutfindo.onmymark.client.marker.ClientMarkerManager;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.marker.PlayerMarker;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OnMyMarkRenderer {
    private MinecraftClient client;
    private final ClientMarkerManager clientMarkerManager;

    private final Map<Marker, MarkerRenderer> markerRendererMap;

    private float fovMultiplier, lastFovMultiplier;
    
    public OnMyMarkRenderer(MinecraftClient client, ClientMarkerManager clientMarkerManager) {
        this.client = client;
        this.clientMarkerManager = clientMarkerManager;

        this.markerRendererMap = new HashMap<>();

        this.fovMultiplier = 1.0f;
        this.lastFovMultiplier = 1.0f;
    }

    /**
     * Performs a render action for the mod. Runs every render tick.
     * @param drawContext Draw networkContext
     * @param tickCounter Tick counter
     */
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();

        if (client.player == null || client.world == null) return;
        else this.client = client;

        // Update the FOV multiplier every render tick
        this.updateFovMultiplier();

        // Render only if available
        if (clientMarkerManager == null) return;

        for (Marker marker : clientMarkerManager.markers()) {
            // Update markers before any rendering is done
            marker.update(client.world);

            MarkerRenderer markerRenderer = this.getOrCreateRenderer(marker);

            if (markerRenderer == null) continue;
            else {
                float tickDelta = tickCounter.getTickProgress(false);
                float fov = this.getFov(camera, tickDelta);

                // Update the marker's position and stuff
                boolean isFovChanging = lastFovMultiplier != fovMultiplier;
                markerRenderer.renderTick(drawContext, tickDelta, fov, isFovChanging);

                if (markerRenderer.shouldDraw()) {
                    markerRenderer.draw(drawContext);
                }
            }
        }

        // Remove any markers that are no longer managed
        Set<Marker> trackedMarkerSet = Set.copyOf(clientMarkerManager.markers());
        Set<Marker> renderedMarkerSet = Set.copyOf(this.markerRendererMap.keySet());

        for (Marker renderedMarker : renderedMarkerSet) {
            if (!trackedMarkerSet.contains(renderedMarker)) {
                markerRendererMap.remove(renderedMarker);
            }
        }
    }

    public Map<Marker, MarkerRenderer> markerRendererMap() {
        return markerRendererMap;
    }

    private MarkerRenderer getOrCreateRenderer(Marker marker) {
        MarkerRenderer renderer = this.markerRendererMap.get(marker);

        if (renderer != null) {
            return renderer;
        }

        MarkerRenderer newRenderer = null;
        if (marker instanceof BlockMarker blockMarker) {
            newRenderer = new BlockMarkerRenderer(client, blockMarker);
            this.markerRendererMap.put(marker, newRenderer);
        } else if (marker instanceof EntityMarker entityMarker) {
            newRenderer = new EntityMarkerRenderer(client, entityMarker);
            this.markerRendererMap.put(marker, newRenderer);
        } else if (marker instanceof PlayerMarker playerMarker) {
            newRenderer = new PlayerMarkerRenderer(client, playerMarker);
            this.markerRendererMap.put(marker, newRenderer);
        }

        if (newRenderer == null) {
            System.err.println("Unexpected state; did you handle all different possible markers?");
        }

        return newRenderer;
    }

    /**
     * Calculates the player's current FOV.
     * Taken from {@link GameRenderer#getFov(Camera, float, boolean)}
     * @param camera Player's camera
     * @param tickDelta Current tick delta
     * @return Current FOV value
     */
    private float getFov(Camera camera, float tickDelta) {
        float f = client.options.getFov().getValue();
        f *= MathHelper.lerp(tickDelta, lastFovMultiplier, fovMultiplier);

        if (camera.getFocusedEntity() instanceof LivingEntity livingEntity && livingEntity.isDead()) {
            float g = Math.min(livingEntity.deathTime + tickDelta, 20.0F);
            f /= (1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F;
        }

        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
            float g = client.options.getFovEffectScale().getValue().floatValue();
            f *= MathHelper.lerp(g, 1.0F, 0.85714287F);
        }

        return f;
    }

    /**
     * Update the FOV multiplier, depending on the player's state.
     * Taken from {@link net.minecraft.client.render.GameRenderer#updateFovMultiplier()}.
     */
    private void updateFovMultiplier() {
        float g;
        if (client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            GameOptions gameOptions = client.options;
            boolean bl = gameOptions.getPerspective().isFirstPerson();
            float f = gameOptions.getFovEffectScale().getValue().floatValue();

            g = abstractClientPlayerEntity.getFovMultiplier(bl, f);
        } else {
            g = 1.0F;
        }

        this.lastFovMultiplier = fovMultiplier;
        // FIXME: Adjust the value here to improve snapping issue between running and walking
        // We set the value to 0.1f to reduce the snapping effect, but there's still some snapping
        fovMultiplier = fovMultiplier + (g - fovMultiplier) * 0.1f;

        fovMultiplier = MathHelper.clamp(fovMultiplier, 0.1F, 1.5F);
    }
}
