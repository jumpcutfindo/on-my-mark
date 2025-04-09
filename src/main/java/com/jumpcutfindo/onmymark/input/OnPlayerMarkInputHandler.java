package com.jumpcutfindo.onmymark.input;

import com.jumpcutfindo.onmymark.client.ClientMarkerManager;
import com.jumpcutfindo.onmymark.client.ClientPartyManager;
import com.jumpcutfindo.onmymark.graphics.OnMyMarkRenderer;
import com.jumpcutfindo.onmymark.graphics.markers.MarkerRenderer;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.marker.Marker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnPlayerMarkInputHandler implements InputHandler {
    private final ClientPartyManager clientPartyManager;
    private final ClientMarkerManager clientMarkerManager;
    private final OnMyMarkRenderer renderer;

    public OnPlayerMarkInputHandler(ClientPartyManager clientPartyManager, ClientMarkerManager clientMarkerManager, OnMyMarkRenderer renderer) {
        this.clientPartyManager = clientPartyManager;
        this.clientMarkerManager = clientMarkerManager;
        this.renderer = renderer;
    }
    
    @Override
    public void execute(MinecraftClient client) {
        if (client == null || client.player == null || !this.clientPartyManager.isInParty()) {
            return;
        }

        // Attempt to remove existing markers first, if any
        List<Marker> markers = findMarkersAroundCrosshair(client)
                .stream()
                .filter(marker -> marker.isOwner(client.player))
                .toList();

        if (!markers.isEmpty()) {
            // Since there are some markers around, we remove them and stop further processing
            clientMarkerManager.removeMarkerOf(this.clientPartyManager.self());
            // TODO: Implement propagation of marker removal to other clients
            return;
        }

        // Attempt to create a marker at the crosshair's target
        HitResult hitResult = findCrosshairTarget(client, client.getCameraEntity(), 1.0f);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            clientMarkerManager.setMarker(this.clientPartyManager.self(), new EntityMarker(this.clientPartyManager.self(), entity));
            // TODO: Implement propagation of marker creation to other clients
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();

            if (client.world == null) return;

            BlockState blockState = client.world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            clientMarkerManager.setMarker(this.clientPartyManager.self(), new BlockMarker(this.clientPartyManager.self(), blockPos, blockState));
            // TODO: Implement propagation of marker creation to other clients
        }
    }

    /**
     * Retrieves the list of markers that are present around a small radius of the crosshair.
     */
    private List<Marker> findMarkersAroundCrosshair(MinecraftClient client) {
        int radius = 8;
        int centerX = client.getWindow().getScaledWidth() / 2;
        int centerY = client.getWindow().getScaledHeight() / 2;

        Map<Marker, MarkerRenderer> markerRendererMap = renderer.markerRendererMap();
        List<Marker> result = new ArrayList<>();

        for (Marker marker : markerRendererMap.keySet()) {
            MarkerRenderer mr = markerRendererMap.get(marker);
            Vector4f mrScreenPos = mr.screenPos();

            // Compute squared distance to avoid sqrt
            float dx = mrScreenPos.x - centerX;
            float dy = mrScreenPos.y - centerY;
            float distanceSq = dx * dx + dy * dy;

            if (distanceSq <= radius * radius) {
                result.add(marker);
            }
        }

        return result;
    }

    /**
     * Retrieves whatever the player is pointing at.
     * Derived from {@link net.minecraft.client.render.GameRenderer#findCrosshairTarget(net.minecraft.entity.Entity, double, double, float)}
     */
    private HitResult findCrosshairTarget(MinecraftClient client, Entity cameraEntity, float tickDelta) {
        if (client == null || client.player == null) return null;

        double d = client.options.getClampedViewDistance() * 16.0d; // Multiply by number of blocks per chunk
        double e = MathHelper.square(d);
        Vec3d vec3d = cameraEntity.getCameraPosVec(tickDelta);

        HitResult hitResult = cameraEntity.raycast(d, tickDelta, false);
        double f = hitResult.getPos().squaredDistanceTo(vec3d);
        if (hitResult.getType() != HitResult.Type.MISS) {
            e = f;
            d = Math.sqrt(f);
        }

        Vec3d vec3d2 = cameraEntity.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float g = 1.0F;
        Box box = cameraEntity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(cameraEntity, vec3d, vec3d3, box, EntityPredicates.EXCEPT_SPECTATOR, e);
        return entityHitResult != null ? entityHitResult : hitResult;
    }
}
