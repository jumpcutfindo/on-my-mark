package com.jumpcutfindo.onmymark.client.graphics.markers;

import com.jumpcutfindo.onmymark.client.graphics.screen.utils.ColorUtils;
import com.jumpcutfindo.onmymark.client.graphics.utils.DrawUtils;
import com.jumpcutfindo.onmymark.client.graphics.utils.RenderMath;
import com.jumpcutfindo.onmymark.marker.Marker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class MarkerRenderer {
    // TODO(preference): Implement adjusting of pointer dimensions
    public static final float POINTER_WIDTH = 6F;
    public static final float POINTER_HEIGHT = 6F;

    public static final float ICON_SIZE = 16F;

    protected MinecraftClient client;
    protected Marker marker;

    // TODO(preference): Implement switching of player selection between circle and oval
    private ClampType clampType;
    private boolean isClamped;
    private float clampWidth, clampHeight;

    protected Vector4f screenPos, prevScreenPos;
    protected Vector2f screenPosNormal;
    protected double distanceFromPlayer;

    private int pointerColor;
    private final PointerShape pointerShape;

    private float existTime;

    protected MarkerRenderer(MinecraftClient client, Marker marker, PointerShape pointerShape) {
        this.client = client;
        this.marker = marker;

        this.screenPos = null;
        this.prevScreenPos = null;

        this.clampType = ClampType.OVAL;

        this.pointerColor = -1;
        this.pointerShape = pointerShape;
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
        return screenPos != null && screenPosNormal != null && this.marker.isVisible(this.client.player);
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

            Vector2f iconPos = this.getClampedLabelPos(this.getLabelWidth(), this.getLabelHeight(),  16F + distanceLabelHeight * screenPosNormal.y);
            this.drawLabel(drawContext, iconPos.x(), iconPos.y(), true);

            this.drawDistanceLabel(drawContext, iconPos.x() + getLabelWidth() / 2F - distanceLabelWidth / 2F, iconPos.y() + getLabelHeight() + 4F, distanceLabelScale);
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

            DrawUtils.drawDiamond(drawContext, x1, y1, x2, y2, x3, y3, x4, y4, color);
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

            // Points: top, left, bottom, right (clockwise)
            float x1 = centerX;
            float y1 = centerY - hh;

            float x2 = centerX - hw;
            float y2 = centerY;

            float x3 = centerX;
            float y3 = centerY + hh;

            float x4 = centerX + hw;
            float y4 = centerY;

            // Optionally, rotate according to `normal` and `perpendicular`
            Vector2f top = new Vector2f(normal).mul(hh).add(centerX, centerY);
            Vector2f bottom = new Vector2f(normal).mul(-hh).add(centerX, centerY);
            Vector2f left = new Vector2f(perpendicular).mul(-hw).add(centerX, centerY);
            Vector2f right = new Vector2f(perpendicular).mul(hw).add(centerX, centerY);

            DrawUtils.drawDiamond(drawContext,
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
        float labelCenterX = offsetPos.x + inwardNormal.x * POINTER_WIDTH + inwardNormal.x * labelWidth / 2;
        float labelCenterY = offsetPos.y + inwardNormal.y * POINTER_HEIGHT + inwardNormal.y * labelHeight / 2;

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

    public enum PointerShape {
        TRIANGLE, DIAMOND
    }
}
