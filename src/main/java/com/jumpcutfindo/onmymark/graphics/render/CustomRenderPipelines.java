package com.jumpcutfindo.onmymark.graphics.render;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;

public class CustomRenderPipelines {
    /**
     * RenderPipeline for drawing an entity with a solid color.
     */
    public static final RenderPipeline SOLID_ENTITY_COLOR = RenderPipeline
            .builder()
            .withUniform("ModelViewMat", UniformType.MATRIX4X4)
            .withUniform("ProjMat", UniformType.MATRIX4X4)
            .withUniform("ColorModulator", UniformType.VEC4)
            .withLocation(Identifier.of(OnMyMarkMod.MOD_ID, "pipeline/solid_entity_color"))
            .withVertexShader(Identifier.of(OnMyMarkMod.MOD_ID, "core/solid_color"))
            .withFragmentShader(Identifier.of(OnMyMarkMod.MOD_ID, "core/solid_color"))
            .withSampler("Sampler0")
            .withColorWrite(true)
            .withDepthWrite(true)
            .withPolygonMode(PolygonMode.FILL)
            .withCull(true)
            .withVertexFormat(
                    VertexFormat.builder()
                            .add("Position", VertexFormatElement.POSITION)
                            .add("Color", VertexFormatElement.COLOR)
                            .add("UV0", VertexFormatElement.UV0)
                            .build(),
                    VertexFormat.DrawMode.QUADS
            )
            .build();
}
