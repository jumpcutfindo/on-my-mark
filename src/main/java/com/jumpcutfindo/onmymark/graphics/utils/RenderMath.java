package com.jumpcutfindo.onmymark.graphics.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RenderMath {
    public static boolean isBehindPlayer(MinecraftClient client, Vec3d worldPos, float fovDegrees) {
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        Vector3f cameraLookVec = camera.getHorizontalPlane();

        Vec3d toTarget = worldPos.subtract(cameraPos).normalize();

        // Dot product: if negative, it's behind
        double dot = cameraLookVec.dot(toTarget.toVector3f());
        return dot < 0;
    }

    public static Vector4f aheadWorldToScreenPos(MinecraftClient client, Vec3d worldPos, float fovDegrees) {
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        // Offset world position by camera position
        Vec3d relativePos = worldPos.subtract(cameraPos);

        // Create 4-D vector for calculations
        Vector4f vec = new Vector4f((float) relativePos.x, (float) relativePos.y, (float) relativePos.z, 1.0f);

        // Apply matrices
        Matrix4f cameraRotationMatrix = new Matrix4f();
        cameraRotationMatrix = camera.getRotation().get(cameraRotationMatrix);
        cameraRotationMatrix = cameraRotationMatrix.transpose();
        vec.mul(cameraRotationMatrix);

        Matrix4f projectionMatrix = new Matrix4f(client.gameRenderer.getBasicProjectionMatrix(fovDegrees));
        vec.mul(projectionMatrix);

        vec.x /= vec.w;
        vec.y /= vec.w;
        vec.z /= vec.w;

        int windowWidth = client.getWindow().getScaledWidth();
        int windowHeight = client.getWindow().getScaledHeight();

        float screenX, screenY;

        if (vec.w <= 0) {
            // Invert across the center
            vec.x = -vec.x;
            vec.y = -vec.y;
        }

        // Convert to screen coordinates
        screenX = (vec.x * 0.5f + 0.5f) * windowWidth;
        screenY = (0.5f - vec.y * 0.5f) * windowHeight;

        return new Vector4f(screenX, screenY, 0, vec.w);
    }

    public static Vector4f clampScreenPosToEllipse(DrawContext drawContext, Vector4f screenPos, float ellipseWidth, float ellipseHeight) {
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();

        float centerX = windowWidth / 2f;
        float centerY = windowHeight / 2f;

        // Oval semi-axes (half-width and half-height)
        float semiMajorAxis = ellipseWidth / 2f;
        float semiMinorAxis = ellipseHeight / 2f;

        float dX = screenPos.x() - centerX;
        float dY = screenPos.y() - centerY;

        // Equation of ellipse: x^2/a^2 + y^2/b^2 = 1
        // Calculate the distance from the center, normalized to the ellipse
        float distanceSquared = (dX * dX) / (semiMajorAxis * semiMajorAxis) + (dY * dY) / (semiMinorAxis * semiMinorAxis);

        if (distanceSquared > 1.0f) {
            // Normalize the delta to the boundary of the oval
            float scale = MathHelper.sqrt(1.0f / distanceSquared); // Scale factor to keep the point on the ellipse

            // Clamp the point to the ellipse's boundary
            dX *= scale;
            dY *= scale;

            // Update the screen position
            return new Vector4f(centerX + dX, centerY + dY, screenPos.z(), screenPos.w());
        }

        return screenPos;
    }

    /**
     * Retrieves the normal to an ellipse
     * @param cx x-coordinate of the ellipse center
     * @param cy y-coordinate of the ellipse center
     * @param rx Horizontal radius of the ellipse
     * @param ry Vertical radius of the ellipse
     * @param x x-coordinate of the point on the ellipse
     * @param y y-coordinate of the point on the ellipse
     * @return Normal to the ellipse at the specified point
     */
    public static Vector2f getNormalToEllipse(float cx, float cy, float rx, float ry, float x, float y) {
        float normalX = (2 * (x - cx)) / (rx * rx);
        float normalY = (2 * (y - cy)) / (ry * ry);

        return new Vector2f(normalX, normalY).normalize();
    }
}
