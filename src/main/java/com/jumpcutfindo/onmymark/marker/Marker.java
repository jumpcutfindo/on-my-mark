package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
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

    public PartyMember owner() {
        return this.owner;
    }

    public boolean isOwner(PlayerEntity player) {
        return this.owner.id().equals(player.getUuid());
    }

    public abstract Vec3d getExactPosition(World world);

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Marker marker = (Marker) o;
        return Objects.equals(id, marker.id) && Objects.equals(owner, marker.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner);
    }
}
