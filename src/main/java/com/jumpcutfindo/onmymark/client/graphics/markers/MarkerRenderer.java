package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.client.graphics.utils.DrawUtils;
import com.jumpcutfindo.onmymark.client.graphics.utils.RenderMath;
import com.jumpcutfindo.onmymark.marker.Marker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.time.Instant;

public abstract class MarkerRenderer {
    public static final int DEFAULT_POINTER_WIDTH = 6, DEFAULT_POINTER_HEIGHT = 6;
    public static final float DEFAULT_LABEL_SCALE = 0.65F;

    public static final float ICON_SIZE = 16F;
    private final Instant creationTime;
    private final PointerShape pointerShape;
    protected MinecraftClient client;
    protected Marker marker;
    protected Vector4f screenPos, prevScreenPos;
    protected Vector2f screenPosNormal;
    protected double distanceFromPlayer;
    private long existTimeMs;
    private boolean isClamped;
    private float clampWidth, clampHeight;

    protected MarkerRenderer(MinecraftClient client, Marker marker, PointerShape pointerShape) {
        this.client = client;
        this.marker = marker;

        this.screenPos = null;
        this.prevScreenPos = null;

        this.pointerShape = pointerShape;

        this.creationTime = Instant.now();
    }

    public Vector4f screenPos() {
        return screenPos;
    }

    public void renderTick(DrawContext drawContext, float tickDelta, float fovDegrees, boolean isFovChanging) {
        // Retrieve config values
        ClampType clampType = OnMyMarkMod.CONFIG.markerClampType();

        this.existTimeMs = Instant.now().toEpochMilli() - creationTime.toEpochMilli();

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
    }

    private void clampScreenPosToCircle(DrawContext drawContext) {
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();

        int smaller = Math.min(windowWidth, windowHeight);
        int larger = Math.max(windowWidth, windowHeight);

        float diameter = Math.clamp(OnMyMarkMod.CONFIG.circleClampDiameter(), 256, larger);

        this.clampWidth = diameter;
        this.clampHeight = diameter;

        RenderMath.ClampResult clampResult = RenderMath.clampScreenPosToEllipse(drawContext, screenPos, diameter, diameter);
        this.screenPos = clampResult.screenPos();
        this.isClamped = clampResult.isClamped();
    }

    private void clampScreenPosToOval(DrawContext drawContext) {
        int padding = OnMyMarkMod.CONFIG.ovalClampPadding();

        int ellipseWidth = (drawContext.getScaledWindowWidth() - 2 * padding);
        int ellipseHeight = drawContext.getScaledWindowHeight() - 2 * padding;

        this.clampWidth = ellipseWidth;
        this.clampHeight = ellipseHeight;

        RenderMath.ClampResult clampResult = RenderMath.clampScreenPosToEllipse(drawContext, screenPos, ellipseWidth, ellipseHeight);
        this.screenPos = clampResult.screenPos();
        this.isClamped = clampResult.isClamped();
    }

    public boolean shouldDraw() {
        return this.getLifetimeMs() > this.existTimeMs
                && screenPos != null
                && screenPosNormal != null
                && this.marker.isVisible(this.client.player);
    }

    abstract Vec3d getWorldPos();

    abstract Vec3d getMarkerWorldPos();

    abstract String getName();

    abstract int getPointerColor();

    abstract boolean isMoving();

    public void draw(DrawContext drawContext) {
        // Calculate distance label variables
        float distanceLabelScale = OnMyMarkMod.CONFIG.distanceLabelScale();
        String distanceLabelString = this.getDistanceLabelString();

        int distanceLabelWidth = (int) ((float) client.textRenderer.getWidth(distanceLabelString) * distanceLabelScale);
        int distanceLabelHeight = (int) ((float) client.textRenderer.fontHeight * distanceLabelScale);

        // Calculate pointer values
        float pointerSizeModifier = existTimeMs >= 1100L ? 1.0F : (float) (Math.abs(Math.sin((((float) existTimeMs / 10F) / 20F)))) * 1.3F;
        float pointerWidth = OnMyMarkMod.CONFIG.markerPointerWidth() * pointerSizeModifier;
        float pointerHeight = OnMyMarkMod.CONFIG.markerPointerHeight() * pointerSizeModifier;
        int pointerColor = ColorHelper.withAlpha((int) Math.min((float) existTimeMs / 20, 1.0F) * 255, this.getPointerColor());

        if (this.isClamped) {

            // Draw edge pointer if the marker has been clamped
            this.drawEdgePointer(drawContext, pointerWidth, pointerHeight, pointerColor);

            Vector2f iconPos = this.getClampedLabelPos(this.getLabelWidth(), this.getLabelHeight(), 16F + distanceLabelHeight * screenPosNormal.y);
            this.drawLabel(drawContext, iconPos.x(), iconPos.y(), true);

            this.drawDistanceLabel(drawContext, iconPos.x() + getLabelWidth() / 2F - distanceLabelWidth / 2F, iconPos.y() + getLabelHeight() + 4F, distanceLabelScale);
        } else {
            // Draw pointer that points directly toward object

            this.drawPointer(drawContext, pointerWidth, pointerHeight, pointerColor);

            float screenX = screenPos.x() - this.getLabelWidth() / 2F;
            float screenY = screenPos.y() - OnMyMarkMod.CONFIG.markerPointerHeight() - getLabelHeight() - 4F;

            this.drawLabel(drawContext, screenX, screenY, true);

            this.drawDistanceLabel(drawContext, screenPos.x() - distanceLabelWidth / 2F, screenPos.y() - OnMyMarkMod.CONFIG.markerPointerHeight() - distanceLabelHeight - 24F, distanceLabelScale);
        }

    }

    private void drawPointer(DrawContext drawContext, float width, float height, int color) {
        // Tip is always at the screen pos
        float x1 = this.screenPos.x();
        float y1 = this.screenPos.y();

        if (pointerShape == PointerShape.TRIANGLE) {
            float x2 = this.screenPos.x - width / 2F;
            float y2 = this.screenPos.y - height;

            float x3 = this.screenPos.x + width / 2F;
            float y3 = this.screenPos.y - height;

            DrawUtils.drawTriangle(drawContext, x1, y1, x2, y2, x3, y3, color);
        } else {
            float x2 = this.screenPos.x - width / 2F;
            float y2 = this.screenPos.y - height / 2F;

            float x3 = this.screenPos.x;
            float y3 = this.screenPos.y - height;

            float x4 = this.screenPos.x + width / 2F;
            float y4 = this.screenPos.y - height / 2F;

            DrawUtils.drawQuad(drawContext, x1, y1, x2, y2, x3, y3, x4, y4, color);
        }

    }

    private String getDistanceLabelString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%dm", (int) distanceFromPlayer));

        // Append an asterisk if not live
        if (this.marker.liveness() == Marker.Liveness.DORMANT) {
            sb.append("*");
        }

        return sb.toString();
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

        float windowCenterX = windowWidth / 2f;
        float windowCenterY = windowHeight / 2f;

        Vector2f normal = RenderMath.getNormalToEllipse(windowCenterX, windowCenterY, clampWidth / 2f, clampHeight / 2f, screenPos.x(), screenPos.y());

        Vector2f perpendicular = new Vector2f(-normal.y, normal.x);

        // Tip
        float tipX = screenPos.x();
        float tipY = screenPos.y();

        if (this.pointerShape == PointerShape.TRIANGLE) {
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

            DrawUtils.drawTriangle(drawContext, tipX, tipY, baseLeftX, baseLeftY, baseRightX, baseRightY, color);
        } else {
            float centerX = tipX - normal.x * height / 2;
            float centerY = tipY - normal.y * height / 2;

            // Half-width and half-height
            float hw = width / 2F;
            float hh = height / 2F;

            // Rotate according to `normal` and `perpendicular`
            Vector2f top = new Vector2f(normal).mul(hh).add(centerX, centerY);
            Vector2f bottom = new Vector2f(normal).mul(-hh).add(centerX, centerY);
            Vector2f left = new Vector2f(perpendicular).mul(-hw).add(centerX, centerY);
            Vector2f right = new Vector2f(perpendicular).mul(hw).add(centerX, centerY);

            DrawUtils.drawQuad(drawContext,
                    top.x, top.y,
                    right.x, right.y,
                    bottom.x, bottom.y,
                    left.x, left.y,
                    color
            );
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
        float labelCenterX = offsetPos.x + inwardNormal.x * OnMyMarkMod.CONFIG.markerPointerWidth() + inwardNormal.x * labelWidth / 2;
        float labelCenterY = offsetPos.y + inwardNormal.y * OnMyMarkMod.CONFIG.markerPointerHeight() + inwardNormal.y * labelHeight / 2;

        // Compute the top-left corner to draw the icon centered at that point
        int iconX = (int) (labelCenterX - labelWidth / 2F);
        int iconY = (int) (labelCenterY - labelHeight / 2F);

        return new Vector2f(iconX, iconY);
    }

    public void drawLabel(DrawContext drawContext, float screenX, float screenY, boolean isOutlined) {
        // Draw the name of the object by default
        drawContext.drawText(client.textRenderer, this.getName(), (int) screenX, (int) screenY, 0xFFFFFF, true);
    }

    protected abstract LabelDisplayType getLabelDisplayType();

    public int getLabelWidth() {
        LabelDisplayType displayType = this.getLabelDisplayType();

        switch (displayType) {
            case ICON -> {
                return (int) MarkerRenderer.ICON_SIZE;
            }
            case TEXT -> {
                return client.textRenderer.getWidth(this.getName());
            }
            case CUSTOM -> {
                throw new RuntimeException("You need to provide a custom width function for custom labels");
            }
        }

        throw new RuntimeException("Unhandled label type");
    }

    public int getLabelHeight() {
        LabelDisplayType displayType = this.getLabelDisplayType();

        switch (displayType) {
            case ICON -> {
                return (int) MarkerRenderer.ICON_SIZE;
            }
            case TEXT -> {
                return client.textRenderer.fontHeight;
            }
            case CUSTOM -> {
                throw new RuntimeException("You need to provide a custom height function for custom labels");
            }
        }

        throw new RuntimeException("Unhandled label type");
    }

    public abstract long getLifetimeMs();

}
