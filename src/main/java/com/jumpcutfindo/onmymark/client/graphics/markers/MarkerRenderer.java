package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ColorUtils;
import com.jumpcutfindo.onmymark.client.graphics.utils.RenderMath;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
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

    protected MinecraftClient client;

    // TODO(preference): Implement switching of player selection between circle and oval
    private ClampType clampType;
    private boolean isClamped;
    private float clampWidth, clampHeight;

    protected Vector4f screenPos, prevScreenPos;
    protected Vector2f screenPosNormal;
    protected double distanceFromPlayer;

    private int pointerColor;

    private float existTime;

    protected MarkerRenderer(MinecraftClient client) {
        this.client = client;

        this.screenPos = null;
        this.prevScreenPos = null;

        this.clampType = ClampType.OVAL;
        this.pointerColor = -1;
    }

    public Vector4f screenPos() {
        return screenPos;
    }

    public void renderTick(DrawContext drawContext, float tickDelta, float fovDegrees, boolean isFovChanging) {
        existTime++;

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

        // Calculate and store distance from player
        this.distanceFromPlayer = this.client.player.getPos().distanceTo(this.getWorldPos());

        // Calculate color if not yet done
        if (this.pointerColor <= -1) {
            this.pointerColor = this.getPointerColor();
        }
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

    abstract Vec3d getWorldPos();

    abstract Vec3d getMarkerWorldPos();

    abstract String getName();

    abstract int getPointerColor();

    abstract boolean isMoving();

    public void draw(DrawContext drawContext) {
        // Calculate distance label variables
        float distanceLabelScale = 0.65F; // TODO(preference): Implement adjusting of distance label size
        String distanceLabelString = this.getDistanceLabelString();

        int distanceLabelWidth = (int) ((float) client.textRenderer.getWidth(distanceLabelString) * distanceLabelScale);
        int distanceLabelHeight = (int) ((float) client.textRenderer.fontHeight * distanceLabelScale);

        // Calculate pointer values
        float pointerSizeModifier = existTime >= 80F ? 1.0F : (float) (Math.abs(Math.sin(existTime / 20F))) * 1.4F;
        float pointerWidth = POINTER_WIDTH * pointerSizeModifier;
        float pointerHeight = POINTER_HEIGHT * pointerSizeModifier;
        int pointerColor = ColorUtils.setOpacity(this.pointerColor, Math.min(existTime / 20, 1.0F));

        // TODO(preference): Implement toggling of pointer
        if (this.isClamped) {
            // Draw edge pointer if the marker has been clamped
            this.drawEdgePointer(drawContext, pointerWidth, pointerHeight, pointerColor);
            Vector2f iconPos = this.getClampedLabelPos(this.getLabelWidth(), this.getLabelHeight(), 24F);
            this.drawLabel(drawContext, iconPos.x(), iconPos.y(), true);

            Vector2f distanceLabelPos = this.getClampedLabelPos(distanceLabelWidth, distanceLabelHeight, 10F);
            this.drawDistanceLabel(drawContext, distanceLabelPos.x(), distanceLabelPos.y(), distanceLabelScale);
        } else {
            // Draw pointer that points directly toward object

            this.drawPointer(drawContext, pointerWidth, pointerHeight, pointerColor);

            float screenX = screenPos.x() - this.getLabelWidth() / 2F;
            float screenY = screenPos.y() - POINTER_HEIGHT - getLabelHeight() - 4F;

            this.drawLabel(drawContext, screenX, screenY, true);

            this.drawDistanceLabel(drawContext, screenPos.x() - distanceLabelWidth / 2F, screenPos.y()  - POINTER_HEIGHT - distanceLabelHeight - 24F, distanceLabelScale);
        }

    }

    private void drawPointer(DrawContext drawContext, float width, float height, int color) {
        float x1 = this.screenPos.x();
        float y1 = this.screenPos.y();

        float x2 = this.screenPos.x - width / 2F;
        float y2 = this.screenPos.y - height;

        float x3 = this.screenPos.x + width / 2F;
        float y3 = this.screenPos.y - height;

        MarkerRenderer.drawTriangle(drawContext, x1, y1, x2, y2, x3, y3, color);
    }

    private String getDistanceLabelString() {
        return String.format("%dm", (int) distanceFromPlayer);
    }

    private void drawDistanceLabel(DrawContext drawContext, float screenX, float screenY, float scale) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(scale, scale, scale);
        drawContext.drawText(this.client.textRenderer, getDistanceLabelString(), (int) (screenX / scale), (int) (screenY / scale), 0xFFFFFFFF, true);
        drawContext.getMatrices().pop();
    }

    /**
     * Draws a pointer where the pointer's tip is on the edge of the clamped shape.
     */
    private void drawEdgePointer(DrawContext drawContext, float width, float height, int color) {
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
        float baseCenterX = tipX - normal.x * height;
        float baseCenterY = tipY - normal.y * width;

        // Base corners (perpendicular from base center)
        float baseOffsetX = perpendicular.x * (width / 2f);
        float baseOffsetY = perpendicular.y * (width / 2f);

        float baseLeftX = baseCenterX - baseOffsetX;
        float baseLeftY = baseCenterY - baseOffsetY;

        float baseRightX = baseCenterX + baseOffsetX;
        float baseRightY = baseCenterY + baseOffsetY;

        MarkerRenderer.drawTriangle(drawContext, tipX, tipY, baseLeftX, baseLeftY, baseRightX, baseRightY, color);
    }

    private static void drawTriangle(DrawContext drawContext, float x1, float y1, float x2, float y2, float x3, float y3, int argb) {
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

    private Vector2f offsetFromScreenPos(float offset) {
        float offsetScreenPosX = screenPos.x() - screenPosNormal.x * offset;
        float offsetScreenPosY = screenPos.y() - screenPosNormal.y * offset;

        return new Vector2f(offsetScreenPosX, offsetScreenPosY);
    }

    protected Vector2f getClampedLabelPos(float labelWidth, float labelHeight, float offset) {
        Vector2f offsetPos = offsetFromScreenPos(offset);

        // Flip the normal vector to go inward
        Vector2f inwardNormal = new Vector2f(screenPosNormal);
        inwardNormal.negate();

        // Compute the center of the icon along the inward direction
        float labelCenterX = offsetPos.x + inwardNormal.x * labelWidth;
        float labelCenterY = offsetPos.y + inwardNormal.y * labelHeight;

        // Compute the top-left corner to draw the icon centered at that point
        int iconX = (int) (labelCenterX - labelWidth / 2F);
        int iconY = (int) (labelCenterY - labelHeight / 2F);

        return new Vector2f(iconX, iconY);
    }

    public void drawLabel(DrawContext drawContext, float screenX, float screenY, boolean isOutlined) {
        // Draw the name of the object by default
        drawContext.drawText(client.textRenderer, this.getName(), (int) screenX, (int) screenY, 0xFFFFFF, true);
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
