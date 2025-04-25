package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class EntityMarker extends Marker {
    private final UUID entityId;
    private final String entityName;

    private final Entity entity;

    public EntityMarker(PartyMember owner, UUID entityId, String entityName, Entity entity) {
        super(owner);
        this.entityId = entityId;
        this.entityName = entityName;
        this.entity = entity;
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
    public Vec3d getExactPosition() {
        return entity.getPos();
    }
}
