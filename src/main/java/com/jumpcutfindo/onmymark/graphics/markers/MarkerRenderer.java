package com.jumpcutfindo.onmymark.graphics.markers;

import com.jumpcutfindo.onmymark.graphics.utils.RenderMath;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class MarkerRenderer {
    // TODO(preference): Implement adjusting of pointer dimensions
    public static final float POINTER_WIDTH = 6F;
    public static final float POINTER_HEIGHT = 6F;

    public static final float ICON_SIZE = 16F;

    private MinecraftClient client;

    // TODO(preference): Implement switching of player selection between circle and oval
    private ClampType clampType;
    private boolean isClamped;
    private float clampWidth, clampHeight;

    protected Vector4f screenPos, prevScreenPos;
    protected Vector2f screenPosNormal;

    protected MarkerRenderer(MinecraftClient client) {
        this.client = client;

        this.screenPos = null;
        this.prevScreenPos = null;

        this.clampType = ClampType.OVAL;
    }

    public Vector4f screenPos() {
        return screenPos;
    }

    public void renderTick(DrawContext drawContext, float tickDelta, float fovDegrees, boolean isFovChanging) {
        // Calculate screen position
        prevScreenPos = screenPos;

        Vec3d markerPos = this.getMarkerWorldPos();

        Vector4f newScreenPos = RenderMath.aheadWorldToScreenPos(client, markerPos, fovDegrees);

        if (prevScreenPos == null) {
            screenPos = newScreenPos;
            return;
        }

        if (isMoving()) {
            // Lerp to smooth movement snapping for markers
            screenPos = new Vector4f(
                    MathHelper.lerp(tickDelta, prevScreenPos.x(), newScreenPos.x()),
                    MathHelper.lerp(tickDelta, prevScreenPos.y(), newScreenPos.y()),
                    MathHelper.lerp(tickDelta, prevScreenPos.z(), newScreenPos.z()),
                    MathHelper.lerp(tickDelta, prevScreenPos.w(), newScreenPos.w())
            );
        } else {
            screenPos = newScreenPos;
        }

        switch (clampType) {
            case OVAL -> this.clampScreenPosToOval(drawContext);
            case CIRCLE -> this.clampScreenPosToCircle(drawContext);
        }

        // Calculate and store normal
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();
        float centerX = windowWidth / 2f;
        float centerY = windowHeight / 2f;
        screenPosNormal = RenderMath.getNormalToEllipse(centerX, centerY, clampWidth / 2f, clampHeight / 2f, screenPos.x(), screenPos.y());
    }

    private void clampScreenPosToCircle(DrawContext drawContext) {
        // TODO(preference): Implement radius width selection
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();

        float diameter = Math.min(windowWidth, windowHeight) / 3f;

        this.clampWidth = diameter;
        this.clampHeight = diameter;

        RenderMath.ClampResult clampResult = RenderMath.clampScreenPosToEllipse(drawContext, screenPos, diameter, diameter);
        this.screenPos = clampResult.screenPos();
        this.isClamped = clampResult.isClamped();
    }

    private void clampScreenPosToOval(DrawContext drawContext) {
        // TODO (preference): Implement padding value selection
        int padding = 50;

        int ellipseWidth = (drawContext.getScaledWindowWidth() - 2 * padding);
        int ellipseHeight = drawContext.getScaledWindowHeight() - 2 * padding;

        this.clampWidth = ellipseWidth;
        this.clampHeight = ellipseHeight;

        RenderMath.ClampResult clampResult = RenderMath.clampScreenPosToEllipse(drawContext, screenPos, ellipseWidth, ellipseHeight);
        this.screenPos = clampResult.screenPos();
        this.isClamped = clampResult.isClamped();
    }

    public boolean shouldDraw() {
        return screenPos != null && screenPosNormal != null;
    }

    abstract Vec3d getMarkerWorldPos();

    abstract String getName();

    abstract boolean isMoving();

    public void draw(DrawContext drawContext) {
        // TODO(preference): Implement toggling of pointer
        if (this.isClamped) {
            // Draw edge pointer if the marker has been clamped
            this.drawEdgePointer(drawContext);
            Vector2f iconPos = this.getClampedLabelPos(drawContext, this.getLabelWidth(), this.getLabelHeight(), 12F);
            this.drawLabel(drawContext, iconPos.x(), iconPos.y());
        } else {
            // Draw pointer that points directly toward object
            this.drawPointer(drawContext);
            this.drawLabel(drawContext, screenPos.x() - this.getLabelWidth() / 2F, screenPos.y() - POINTER_HEIGHT - getLabelHeight() - 4F);
        }

    }

    private void drawPointer(DrawContext drawContext) {
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, this.screenPos.x, this.screenPos.y, 5).color(0xFFFFFFFF);
        buffer.vertex(transformationMatrix, this.screenPos.x - POINTER_WIDTH / 2F, this.screenPos.y - POINTER_HEIGHT, 5).color(0xFFFFFFFF);
        buffer.vertex(transformationMatrix, this.screenPos.x + POINTER_WIDTH / 2F, this.screenPos.y - POINTER_HEIGHT, 5).color(0xFFFFFFFF);
        buffer.vertex(transformationMatrix, this.screenPos.x, this.screenPos.y, 5).color(0xFFFFFFFF);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    /**
     * Draws a pointer where the pointer's tip is on the edge of the clamped shape.
     */
    private void drawEdgePointer(DrawContext drawContext) {
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();

        float centerX = windowWidth / 2f;
        float centerY = windowHeight / 2f;

        Vector2f normal = RenderMath.getNormalToEllipse(centerX, centerY, clampWidth / 2f, clampHeight / 2f, screenPos.x(), screenPos.y());

        Vector2f perpendicular = new Vector2f(-normal.y, normal.x);

        // Tip of the triangle
        float tipX = screenPos.x();
        float tipY = screenPos.y();

        // Base center point (back from tip along the normal)
        float baseCenterX = tipX - normal.x * POINTER_HEIGHT;
        float baseCenterY = tipY - normal.y * POINTER_WIDTH;

        // Base corners (perpendicular from base center)
        float baseOffsetX = perpendicular.x * (POINTER_WIDTH / 2f);
        float baseOffsetY = perpendicular.y * (POINTER_WIDTH / 2f);

        float baseLeftX = baseCenterX - baseOffsetX;
        float baseLeftY = baseCenterY - baseOffsetY;

        float baseRightX = baseCenterX + baseOffsetX;
        float baseRightY = baseCenterY + baseOffsetY;

        // Draw triangle
        Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, tipX, tipY, 5).color(0xFFFFFFFF);
        buffer.vertex(transformationMatrix, baseLeftX, baseLeftY, 5).color(0xFFFFFFF);
        buffer.vertex(transformationMatrix, baseRightX, baseRightY, 5).color(0xFFFFFFF);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    private Vector2f offsetFromScreenPos(DrawContext drawContext, float offset) {
        float offsetScreenPosX = screenPos.x() - screenPosNormal.x * offset;
        float offsetScreenPosY = screenPos.y() - screenPosNormal.y * offset;

        return new Vector2f(offsetScreenPosX, offsetScreenPosY);
    }

    protected Vector2f getClampedLabelPos(DrawContext drawContext, float labelWidth, float labelHeight, float offset) {
        Vector2f offsetPos = offsetFromScreenPos(drawContext, offset);

        // Flip the normal vector to go inward
        Vector2f inwardNormal = new Vector2f(screenPosNormal);
        inwardNormal.negate();

        // Compute the center of the icon along the inward direction
        float iconCenterX = offsetPos.x + inwardNormal.x * labelWidth;
        float iconCenterY = offsetPos.y + inwardNormal.y * labelHeight;

        // Compute the top-left corner to draw the icon centered at that point
        int iconX = (int) (iconCenterX - labelWidth / 2F);
        int iconY = (int) (iconCenterY - labelHeight / 2F);

        return new Vector2f(iconX, iconY);
    }

    public void drawLabel(DrawContext drawContext, float screenX, float screenY) {
        // Draw the name of the object by default
        drawContext.drawText(client.textRenderer, this.getName(), (int) screenX, (int) screenY, 0xFFFFFFFF, true);
    }

    public int getLabelWidth() {
        return client.textRenderer.getWidth(this.getName());
    }

    public int getLabelHeight() {
        return client.textRenderer.fontHeight;
    }

    public enum ClampType {
        CIRCLE, OVAL
    }
}
