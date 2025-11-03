package com.jumpcutfindo.onmymark.client.graphics.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(value = EnvType.CLIENT)
public record SolidTriangleGuiElementRenderState(Matrix3x2f pose,
                                                 int x0, int y0, int x1, int y1, int x2, int y2, int argb,
                                                 @Nullable ScreenRect scissorArea,
                                                 @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
    public SolidTriangleGuiElementRenderState(Matrix3x2f pose, int x0, int y0, int x1, int y1, int x2, int y2, int argb, @Nullable ScreenRect scissorArea) {
        this(pose, x0, y0, x1, y1, x2, y2, argb, scissorArea, createBounds(x0, y0, x1, y1, x2, y2, pose, scissorArea));
    }

    @Nullable
    private static ScreenRect createBounds(int x0, int y0, int x1, int y1, int x2, int y2, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        int minX = Math.min(Math.min(x0, x1), x2);
        int minY = Math.min(Math.min(y0, y1), y2);
        int maxX = Math.max(Math.max(x0, x1), x2);
        int maxY = Math.max(Math.max(y0, y1), y2);

        ScreenRect screenRect = new ScreenRect(minX, minY, maxX - minX, maxY - minY).transformEachVertex(pose);

        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        vertices.vertex(this.pose(), (float) this.x0(), (float) this.y0(), depth).color(this.argb());
        vertices.vertex(this.pose(), (float) this.x2(), (float) this.y2(), depth).color(this.argb());
        vertices.vertex(this.pose(), (float) this.x1(), (float) this.y1(), depth).color(this.argb());
        vertices.vertex(this.pose(), (float) this.x1(), (float) this.y1(), depth).color(this.argb());
    }

    @Override
    public RenderPipeline pipeline() {
        return RenderPipelines.GUI;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.empty();
    }

    @Override
    @Nullable
    public ScreenRect scissorArea() {
        return this.scissorArea;
    }

    @Override
    @Nullable
    public ScreenRect bounds() {
        return this.bounds;
    }
}

