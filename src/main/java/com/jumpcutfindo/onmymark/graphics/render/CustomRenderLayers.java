package com.jumpcutfindo.onmymark.graphics.render;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

import java.util.function.Function;

public class CustomRenderLayers {
    /**
     * RenderLayer for drawing an entity with only a single color.
     */
    public static final Function<Identifier, RenderLayer> SOLID_ENTITY_COLOR;

    static {
        SOLID_ENTITY_COLOR = Util.memoize((texture) -> {
            RenderLayer.MultiPhaseParameters multiPhaseParameters =
                    RenderLayer.MultiPhaseParameters.builder()
                            .texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
                            .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                            .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                            .build(false);
            return RenderLayer.of(Identifier.of(OnMyMarkMod.MOD_ID, "solid_entity_color").toString(), 1536, true, false, CustomRenderPipelines.SOLID_ENTITY_COLOR, multiPhaseParameters);
        });;
    }
}
