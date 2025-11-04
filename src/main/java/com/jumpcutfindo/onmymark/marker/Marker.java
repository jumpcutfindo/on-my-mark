package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

public abstract class Marker {
    private final UUID id;
    private final RegistryKey<World> worldRegistryKey;
    private final Vec3d initialPos;
    private PartyMember owner;
    private Vec3d lastPos;

    private Liveness liveness;

    public Marker(PartyMember owner, RegistryKey<World> worldRegistryKey, Vec3d initialPos) {
        this.id = UUID.randomUUID();
        this.owner = owner;

        this.worldRegistryKey = worldRegistryKey;

        this.initialPos = initialPos;
        this.lastPos = initialPos;

        this.liveness = Liveness.DORMANT;
    }

    public PartyMember owner() {
        return this.owner;
    }

    public void setOwner(PartyMember partyMember) {
        this.owner = partyMember;
    }

    public RegistryKey<World> worldRegistryKey() {
        return worldRegistryKey;
    }

    public Vec3d lastPos() {
        return lastPos;
    }

    public void setLastPos(Vec3d lastPos) {
        this.lastPos = lastPos;
    }

    public Liveness liveness() {
        return liveness;
    }

    protected void setLiveness(Liveness liveness) {
        this.liveness = liveness;
    }

    public boolean isOwner(PlayerEntity player) {
        return this.owner.id().equals(player.getUuid());
    }

    public boolean isSameDimension(PlayerEntity player) {
        return player.getEntityWorld().getRegistryKey().equals(worldRegistryKey);
    }

    /**
     * Determines whether the marker is visible
     */
    public boolean isVisible(PlayerEntity player) {
        return this.isSameDimension(player);
    }

    public abstract Vec3d getExactPosition(World world);

    public abstract void update(World world);

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

    /**
     * Liveness indicates whether the marker can be seen on the thread that is
     * using it.
     * Should primarily be used by the client.
     */
    public enum Liveness {
        LIVE, DORMANT
    }
}
