package com.jumpcutfindo.onmymark.graphics.render;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

public class CustomRenderLayers {
    /**
     * RenderLayer for drawing an entity with only a single color.
     */
    public static final BiFunction<Identifier, Integer, RenderLayer> SOLID_ENTITY_COLOR;

    static {
        SOLID_ENTITY_COLOR = Util.memoize((texture, color) -> {
            RenderLayer.MultiPhaseParameters multiPhaseParameters =
                    RenderLayer.MultiPhaseParameters.builder()
                            .texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
                            .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                            .layering(new RenderPhase.Layering("custom_color", () -> {
                                // Sets the color
                                float a = ((color >> 24) & 0xFF) / 255f;
                                float r = ((color >> 16) & 0xFF) / 255f;
                                float g = ((color >> 8) & 0xFF) / 255f;
                                float b = (color & 0xFF) / 255f;

                                RenderSystem.setShaderColor(r, g, b, a);
                            }, () -> {
                                // Resets the color
                                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                            }))
                            .build(false);
            return RenderLayer.of(Identifier.of(OnMyMarkMod.MOD_ID, "solid_entity_color").toString(), 1536, true, false, CustomRenderPipelines.SOLID_ENTITY_COLOR, multiPhaseParameters);
        });;
    }
}
