package com.jumpcutfindo.onmymark.graphics;

import com.jumpcutfindo.onmymark.client.ClientMarkerManager;
import com.jumpcutfindo.onmymark.graphics.utils.ObjectDrawer;
import com.jumpcutfindo.onmymark.graphics.utils.RenderMath;
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
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public class OnMyMarkRenderer {
    private MinecraftClient client;
    private float fovMultiplier, lastFovMultiplier;
    
    public OnMyMarkRenderer(MinecraftClient client) {
        this.client = client;

        this.fovMultiplier = 1.0f;
        this.lastFovMultiplier = 1.0f;
    }

    /**
     * Performs a render action for the mod. Runs every render tick.
     * @param drawContext Draw context
     * @param tickCounter Tick counter
     */
    public void render(ClientMarkerManager clientMarkerManager, DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null) return;
        else this.client = client;

        Camera camera = client.gameRenderer.getCamera();

        // Update the FOV multiplier every render tick
        this.updateFovMultiplier();

        // TODO: Implement handling of markers and drawing of markers

        // FIXME: Remove this debug example
        // Example world position
        Vec3d worldPos = new Vec3d(-67.5, 86, -33.5);
        float fovMultiplier = this.getFov(camera, tickCounter.getTickDelta(true));

        Vector4f screenPos = RenderMath.worldToScreenPos(client, worldPos, fovMultiplier);

        ObjectDrawer.drawTriangle(drawContext, screenPos.x(), screenPos.y(), 30, 0xFF0000FF);
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
