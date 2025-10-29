package com.jumpcutfindo.onmymark.client.graphics.utils;

import com.jumpcutfindo.onmymark.client.graphics.render.CustomRenderLayers;
import com.jumpcutfindo.onmymark.client.graphics.render.SolidQuadGuiElementRenderState;
import com.jumpcutfindo.onmymark.client.graphics.render.SolidTriangleGuiElementRenderState;
import com.jumpcutfindo.onmymark.mixin.ItemRenderStateMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

/**
 * Provides some additional utilities on top of Minecraft's DrawContext.
 */
public class DrawUtils {
    /**
     * Draws a provided ItemStack with an outline. Derived from DrawContext#drawItem (Minecraft's private
     * draw method in DrawContext)
     */
    public static void drawItemOutlined(DrawContext drawContext, ItemStack stack, int x, int y, int outlineColor) {
        ItemRenderState itemRenderState = new ItemRenderState();

        World world = MinecraftClient.getInstance().world;
        LivingEntity entity = MinecraftClient.getInstance().player;
        Matrix3x2fStack matrices = drawContext.getMatrices();

        if (!stack.isEmpty()) {
            // Draw the outline as the item but in the background
            MinecraftClient.getInstance().getItemModelManager().clearAndUpdate(itemRenderState, stack, ItemDisplayContext.GUI, world, entity, 1);

            matrices.pushMatrix();
            matrices.translate((float) (x + 8), (float) (y + 8));

            try {
                // Replace existing RenderLayers with solid RenderLayers
                ItemRenderState.LayerRenderState[] layers = ((ItemRenderStateMixin) itemRenderState).layers();
                for (ItemRenderState.LayerRenderState layer : layers) {
                    layer.setRenderLayer(CustomRenderLayers.SOLID_ENTITY_COLOR.apply(outlineColor));
                }

                matrices.scale(18.0F, -18.0F);
                matrices.translate(0.0F, 0.0F);

                drawContext.state.addItem(new ItemGuiElementRenderState(stack.getItem().getName().toString(), new Matrix3x2f(matrices), itemRenderState, x, y, drawContext.scissorStack.peekLast()));
            } catch (Throwable var11) {
                CrashReport crashReport = CrashReport.create(var11, "Rendering solid colored item");
                CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
                throw new CrashException(crashReport);
            }

            matrices.popMatrix();

            // Draw item normally
            drawContext.drawItem(stack, x, y);
        }
    }

    /**
     * Draws a triangle with the specified dimensions.
     */
    public static void drawTriangle(DrawContext drawContext, int x0, int y0, int x1, int y1, int x2, int y2, int argb) {
        var triangle = new SolidTriangleGuiElementRenderState(
                new Matrix3x2f(drawContext.getMatrices()), x0, y0, x1, y1, x2, y2, argb, drawContext.scissorStack.peekLast()
        );
        drawContext.state.addSimpleElement(triangle);
    }

    /**
     * Draws a quad with the specified dimensions.
     */
    public static void drawQuad(DrawContext drawContext, int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3, int argb) {
        // Create simple element
        var quad = new SolidQuadGuiElementRenderState(
                new Matrix3x2f(drawContext.getMatrices()), x0, y0, x1, y1, x2, y2, x3, y3, argb, drawContext.scissorStack.peekLast()
        );
        drawContext.state.addSimpleElement(quad);
    }
}
