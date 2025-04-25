package com.jumpcutfindo.onmymark.client.graphics.utils;

import com.jumpcutfindo.onmymark.client.graphics.render.CustomRenderLayers;
import com.jumpcutfindo.onmymark.mixin.DrawContextMixin;
import com.jumpcutfindo.onmymark.mixin.ItemRenderStateMixin;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

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
        Identifier texture = stack.get(DataComponentTypes.ITEM_MODEL);

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
}
