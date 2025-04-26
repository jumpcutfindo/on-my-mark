package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityMarker extends Marker {
    private final UUID entityId;
    private final String entityName;

    private Entity entity;

    public EntityMarker(PartyMember owner, RegistryKey<World> worldRegistryKey, UUID entityId, String entityName, Vec3d lastPos) {
        super(owner, worldRegistryKey, lastPos);
        this.entityId = entityId;
        this.entityName = entityName;
    }

    public void update(World world) {
        this.entity = world.getEntity(entityId);

        if (entity == null) {
            this.setLiveness(Liveness.DORMANT);
        } else {
            this.setLiveness(Liveness.LIVE);
        }
    }

    public Entity entity() {
        return entity;
    }

    public UUID entityId() {
        return entityId;
    }

    public String entityName() {
        return entityName;
    }

    @Override
    public Vec3d getExactPosition(World world) {
        Entity entity = this.entity();

        if (entity != null) {
            this.setLastPos(entity.getPos());
        }

        return entity != null ? entity.getPos() : this.lastPos();
    }
}
