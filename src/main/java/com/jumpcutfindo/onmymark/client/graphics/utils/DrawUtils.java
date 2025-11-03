package com.jumpcutfindo.onmymark.client.graphics.utils;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.graphics.render.SolidQuadGuiElementRenderState;
import com.jumpcutfindo.onmymark.client.graphics.render.SolidTriangleGuiElementRenderState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2f;

/**
 * Provides some additional utilities on top of Minecraft's DrawContext.
 */
public class DrawUtils {
    /**
     * Draws a provided ItemStack with an outline. Derived from DrawContext#drawItem (Minecraft's private
     * draw method in DrawContext)
     */
    public static void drawItemTiled(DrawContext drawContext, ItemStack stack, int x, int y, int outlineColor) {
        int backgroundAlpha = 96; // 0 - 255
        int backgroundColor = (outlineColor & 0x00FFFFFF) | (backgroundAlpha << 24);

        int borderAlpha = 255; // 0 - 255
        int borderColor = (outlineColor & 0x00FFFFFF) | (borderAlpha << 24);

        // Draw a square outline
        if (OnMyMarkMod.CONFIG.isIconTileVisible()) {
            drawContext.fill(x - 1, y - 1, x + 17, y + 17, backgroundColor);
            drawContext.drawBorder(x - 1, y - 1, 18, 18, borderColor);
        }

        // Draw item normally
        drawContext.drawItem(stack, x, y);
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
