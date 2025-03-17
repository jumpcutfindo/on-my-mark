package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public abstract class Marker {
    private final UUID id;
    private final PartyMember owner;
    // TODO: Add handling for marker expiry
//    private final long expiryTick;

    public Marker(PartyMember owner) {
        this.id = UUID.randomUUID();
        this.owner = owner;


    }

    public abstract Vec3d getExactPosition();
}
