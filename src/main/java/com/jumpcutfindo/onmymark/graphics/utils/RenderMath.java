package com.jumpcutfindo.onmymark.graphics.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class RenderMath {
    public static Vector4f worldToScreenPos(MinecraftClient client, Vec3d worldPos, float fovDegrees) {
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

        // Perspective divide
        // If w is negative, is behind player
        if (vec.w <= 0) return new Vector4f();

        vec.x /= vec.w;
        vec.y /= vec.w;
        vec.z /= vec.w;

        // Convert to screen coordinates
        float screenX = (vec.x * 0.5f + 0.5f) * client.getWindow().getScaledWidth();
        float screenY = (0.5f - vec.y * 0.5f) * client.getWindow().getScaledHeight();

        return new Vector4f(screenX, screenY, 0, 1);
    }
}
