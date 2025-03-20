package com.jumpcutfindo.onmymark.graphics.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class ObjectDrawer {
    /**
     * Draws a triangle where the vertical axis of the triangle is centered at the specified xy-coordinate
     * @param drawContext Draw context
     * @param screenX Screen x-coordinates
     * @param screenY Screen y-coordinates
     * @param size Size of the triangle
     * @param color Color of the triangle
     */
    public static void drawTriangle(DrawContext drawContext, float screenX, float screenY, int size, int color) {
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        // Initialize the buffer using the specified format and draw mode.
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        // Write our vertices, Z doesn't really matter since it's on the HUD.

        buffer.vertex(transformationMatrix, screenX - (float) size / 2.0f, screenY, 5).color(color);
        buffer.vertex(transformationMatrix, screenX, screenY + (float) size / 2.0f, 5).color(color);
        buffer.vertex(transformationMatrix, screenX, screenY, 5).color(0xFF00FF00);
        buffer.vertex(transformationMatrix, screenX + (float) size / 2.0f, screenY, 5).color(color);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}
