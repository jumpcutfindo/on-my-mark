package com.jumpcutfindo.onmymark.client.input;

import com.jumpcutfindo.onmymark.client.network.ClientNetworkSender;
import com.jumpcutfindo.onmymark.client.party.ClientPartyManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlaceMarkerInputHandler extends InputHandler {

    private final ClientPartyManager clientPartyManager;

    public PlaceMarkerInputHandler(MinecraftClient client, ClientPartyManager clientPartyManager) {
        super(client);
        this.clientPartyManager = clientPartyManager;
    }

    @Override
    public int maxConcurrentInputs() {
        return 3;
    }

    @Override
    public long inputDelayMs() {
        return 2000L;
    }

    @Override
    public boolean execute() {
        super.execute();

        if (client == null || client.player == null) {
            return false;
        }

        if (!this.clientPartyManager.isInParty()) {
            client.player.sendMessage(Text.translatable("text.action.onmymark.placeMarker.notInParty"), false);
            return false;
        }

        return this.tryPlaceMarker();
    }

    private boolean tryPlaceMarker() {
        // Attempt to create a marker at the crosshair's target
        HitResult hitResult = findCrosshairTarget(client, client.getCameraEntity(), 1.0f);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            ClientNetworkSender.markEntity(client.player, entity);
            return true;
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();

            ClientNetworkSender.markBlock(client.player, blockPos);
            return true;
        }

        return false;
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
