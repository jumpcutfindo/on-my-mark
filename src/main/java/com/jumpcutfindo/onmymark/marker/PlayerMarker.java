package com.jumpcutfindo.onmymark.marker;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class PlayerMarker extends Marker {
    private final UUID playerId;
    private final String playerName;

    private PlayerEntity player;

    public PlayerMarker(PartyMember owner, RegistryKey<World> worldRegistryKey, UUID playerId, String playerName, Vec3d lastPos) {
        super(owner, worldRegistryKey, lastPos);

        this.playerId = playerId;
        this.playerName = playerName;
    }

    public UUID playerId() {
        return playerId;
    }

    public String playerName() {
        return playerName;
    }

    public PlayerEntity player() {
        return player;
    }

    @Override
    public Vec3d getExactPosition(World world) {
        if (this.player != null) {
            this.setLastPos(player.getEntityPos());
        }

        return player != null ? player.getEntityPos() : this.lastPos();
    }

    @Override

    public void update(World world) {
        this.player = world.getPlayerByUuid(this.playerId);

        if (player == null) {
            this.setLiveness(Liveness.DORMANT);
        } else {
            this.setLiveness(Liveness.LIVE);
        }
    }
}
