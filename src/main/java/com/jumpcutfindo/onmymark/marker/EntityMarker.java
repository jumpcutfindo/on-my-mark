package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityMarker extends Marker {
    private final Entity entity;

    public EntityMarker(PartyMember owner, Entity entity) {
        super(owner);
        this.entity = entity;
    }

    @Override
    public Vec3d getExactPosition() {
        return entity.getPos();
    }
}
