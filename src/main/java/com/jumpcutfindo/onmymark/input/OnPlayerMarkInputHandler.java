package com.jumpcutfindo.onmymark.input;

import com.jumpcutfindo.onmymark.client.ClientMarkerManager;
import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
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

public class OnPlayerMarkInputHandler implements InputHandler {
    private final ClientMarkerManager clientMarkerManager;

    public OnPlayerMarkInputHandler(ClientMarkerManager clientMarkerManager) {
        this.clientMarkerManager = clientMarkerManager;
    }
    
    @Override
    public void execute(MinecraftClient client) {
        AbstractClientPlayerEntity clientPlayer = client.player;
        HitResult hitResult = findCrosshairTarget(client, client.getCameraEntity(), 1.0f);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            System.out.println(entity.getDisplayName());

            // TODO: Change this so that the party member is some instance
            clientMarkerManager.addMarker(new EntityMarker(new PartyMember(clientPlayer), entity));
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();

            if (client.world == null) return;

            BlockState blockState = client.world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            System.out.println(block.getName());

            // TODO: Change this so that the party member is some instance
            clientMarkerManager.addMarker(new BlockMarker(new PartyMember(clientPlayer), blockPos, blockState));
        } else if (hitResult.getType() == HitResult.Type.MISS) {
            System.out.println("lol you missed");
        }
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
        EntityHitResult entityHitResult = ProjectileUtil.raycast(cameraEntity, vec3d, vec3d3, box, EntityPredicates.CAN_HIT, e);
        return entityHitResult != null ? entityHitResult : hitResult;
    }
}
