package com.jumpcutfindo.onmymark.client.graphics.utils;

import com.jumpcutfindo.onmymark.client.graphics.render.CustomRenderLayers;
import com.jumpcutfindo.onmymark.mixin.DrawContextMixin;
import com.jumpcutfindo.onmymark.mixin.ItemRenderStateMixin;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.joml.Matrix4f;

/**
 * Provides some additional utilities on top of Minecraft's DrawContext.
 */
public class DrawUtils {
    /**
     * Draws a provided ItemStack with an outline. Derived from {@link DrawContext#drawItem} (Minecraft's private
     * draw method in DrawContext)
     */
    public static void drawItemOutlined(DrawContext drawContext, ItemStack stack, int x, int y, int outlineColor) {
        VertexConsumerProvider.Immediate vertexConsumers = ((DrawContextMixin) drawContext).vertexConsumers();
        ItemRenderState itemRenderState = ((DrawContextMixin) drawContext).itemRenderState();

        World world = MinecraftClient.getInstance().world;
        LivingEntity entity = MinecraftClient.getInstance().player;
        MatrixStack matrices = drawContext.getMatrices();

        if (!stack.isEmpty()) {
            // Draw the outline as the item but in the background
            MinecraftClient.getInstance().getItemModelManager().clearAndUpdate(itemRenderState, stack, ItemDisplayContext.GUI, world, entity, 1);

            matrices.push();
            matrices.translate((float)(x + 8), (float)(y + 8), (float)(150));

            try {
                // Replace existing RenderLayers with solid RenderLayers
                ItemRenderState.LayerRenderState[] layers = ((ItemRenderStateMixin) itemRenderState).layers();
                for (ItemRenderState.LayerRenderState layer : layers) {
                    layer.setRenderLayer(CustomRenderLayers.SOLID_ENTITY_COLOR.apply(outlineColor));
                }

                matrices.scale(18.0F, -18.0F, 1.0F);
                matrices.translate(0.0F, 0.0F, -32.0F);

                itemRenderState.render(matrices, vertexConsumers, 15728880, OverlayTexture.DEFAULT_UV);

                vertexConsumers.draw();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } catch (Throwable var11) {
                CrashReport crashReport = CrashReport.create(var11, "Rendering solid colored item");
                CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
                throw new CrashException(crashReport);
            }

            matrices.pop();

            // Draw item normally
              drawContext.drawItem(stack, x, y);
        }
    }


    /**
     * Draws a triangle with the specified dimensions.
     */
    public static void drawTriangle(DrawContext drawContext, float x1, float y1, float x2, float y2, float x3, float y3, int argb) {
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, x1, y1, 5).color(argb);
        buffer.vertex(transformationMatrix, x2, y2, 5).color(argb);
        buffer.vertex(transformationMatrix, x3, y3, 5).color(argb);

        try (BuiltBuffer builtBuffer = buffer.end()) {
            RenderLayer.getDebugTriangleFan().draw(builtBuffer);
        }
    }

    /**
     * Draws a diamond with the specified dimensions.
     */
    public static void drawDiamond(DrawContext drawContext, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int argb) {
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, x1, y1, 5).color(argb);
        buffer.vertex(transformationMatrix, x2, y2, 5).color(argb);
        buffer.vertex(transformationMatrix, x3, y3, 5).color(argb);
        buffer.vertex(transformationMatrix, x4, y4, 5).color(argb);

        try (BuiltBuffer builtBuffer = buffer.end()) {
            RenderLayer.getDebugTriangleFan().draw(builtBuffer);
        }
    }
}
