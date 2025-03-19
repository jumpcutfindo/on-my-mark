package com.jumpcutfindo.onmymark.graphics;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

import java.lang.Math;

public class MarkerDrawer implements ClientModInitializer {
    private static float PREV_FOV_MULTIPLIER = 1.0f;
    private static float FOV_MULTIPLIER = 1.0f;

    @Override
    public void onInitializeClient() {
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerAfter(IdentifiedLayer.MISC_OVERLAYS, Identifier.of(OnMyMarkMod.MOD_ID, "example_layer_after_misc_overlays"), (drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;

            // Example world position
            Vec3d worldPos = new Vec3d(-67.5, 86, -33.5);

            Camera camera = client.gameRenderer.getCamera();
            Vec3d cameraPos = camera.getPos();

            // Offset world position by camera position
            Vec3d relativePos = worldPos.subtract(cameraPos);

            // Create 4-D vector for calculations
            Vector4f vec = new Vector4f((float) relativePos.x, (float) relativePos.y, (float) relativePos.z, 1.0f);

            // Apply matrices
            Matrix4f cameraRotationMatrix = new Matrix4f();
            cameraRotationMatrix = camera.getRotation().get(cameraRotationMatrix);
            cameraRotationMatrix = cameraRotationMatrix.transpose();
            vec.mul(cameraRotationMatrix);

            Matrix4f projectionMatrix = new Matrix4f(client.gameRenderer.getBasicProjectionMatrix(getFov(client, camera, tickCounter.getTickDelta(true))));
            vec.mul(projectionMatrix);

            // Perspective divide
            if (vec.w <= 0) return;
            vec.x /= vec.w;
            vec.y /= vec.w;
            vec.z /= vec.w;

            // Convert to screen coordinates
            float screenX = (vec.x * 0.5f + 0.5f) * client.getWindow().getScaledWidth();
            float screenY = (0.5f - vec.y * 0.5f) * client.getWindow().getScaledHeight();

            Vector4f screenPos = new Vector4f(screenX, screenY, 0, 1);

            int x = (int) screenPos.x();
            int y = (int) screenPos.y();

            drawTriangle(drawContext, x, y, 16, 0xFFFF0000);
        }));
    }

    private static float getFov(MinecraftClient client, Camera camera, float tickDelta) {
        updateFovMultiplier(client);

        float f = client.options.getFov().getValue();
        f *= MathHelper.lerp(tickDelta, PREV_FOV_MULTIPLIER, FOV_MULTIPLIER);

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

    private static void updateFovMultiplier(MinecraftClient client) {
        float g;
        if (client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            GameOptions gameOptions = client.options;
            boolean bl = gameOptions.getPerspective().isFirstPerson();
            float f = gameOptions.getFovEffectScale().getValue().floatValue();

            g = abstractClientPlayerEntity.getFovMultiplier(bl, f);
        } else {
            g = 1.0F;
        }

        PREV_FOV_MULTIPLIER = FOV_MULTIPLIER;
        FOV_MULTIPLIER = FOV_MULTIPLIER + (g - FOV_MULTIPLIER) * 0.1f;
        FOV_MULTIPLIER = MathHelper.clamp(FOV_MULTIPLIER, 0.1F, 1.5F);
    }

    private static float getFovMultiplier(AbstractClientPlayerEntity player, boolean firstPerson, float fovEffectScale) {
        float f = 1.0F;
        if (player.getAbilities().flying) {
            f *= 1.1F;
        }

        float g = player.getAbilities().getWalkSpeed();
        if (g != 0.0F) {
            float h = (float) player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) / g;
            f *= (h + 1.0F) / 2.0F;
        }

        if (player.isUsingItem()) {
            if (player.getActiveItem().isOf(Items.BOW)) {
                float h = Math.min(player.getItemUseTime() / 20.0F, 1.0F);
                f *= 1.0F - MathHelper.square(h) * 0.15F;
            } else if (firstPerson && player.isUsingSpyglass()) {
                return 0.1F;
            }
        }

        return MathHelper.lerp(fovEffectScale, 1.0F, f);
    }

    private static void drawTriangle(DrawContext drawContext, int screenX, int screenY, int size, int color) {
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        // Initialize the buffer using the specified format and draw mode.
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        // Write our vertices, Z doesn't really matter since it's on the HUD.
        float x = screenX;
        float y = screenY;

        buffer.vertex(transformationMatrix, x - (float) size / 2.0f, y, 5).color(color);
        buffer.vertex(transformationMatrix, x, y + (float) size / 2.0f, 5).color(color);
        buffer.vertex(transformationMatrix, x, y, 5).color(0xFF00FF00);
        buffer.vertex(transformationMatrix, x + (float) size / 2.0f, y, 5).color(color);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}
