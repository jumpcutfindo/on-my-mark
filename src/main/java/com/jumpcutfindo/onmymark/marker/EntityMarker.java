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

    public EntityMarker(PartyMember owner, RegistryKey<World> worldRegistryKey, UUID entityId, String entityName) {
        super(owner, worldRegistryKey);
        this.entityId = entityId;
        this.entityName = entityName;
    }

    public Entity entity(World world) {
        return world.getEntity(entityId);
    }

    public UUID entityId() {
        return entityId;
    }

    public String entityName() {
        return entityName;
    }

    @Override
    public Vec3d getExactPosition(World world) {
        return this.entity(world).getPos();
    }
}
