package com.jumpcutfindo.onmymark.graphics.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
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
}
