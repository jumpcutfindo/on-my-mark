package com.jumpcutfindo.onmymark.utils;

import com.jumpcutfindo.onmymark.network.server.ServerPacketContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityUtils {
    /**
     * Retrieves an entity by their UUID.
     * @param world The world of the entity
     * @param pos The reference pos to start the search from
     * @param entityId The UUID of the entity
     * @return The entity, if it exists
     */
    public static Entity getEntityByUuid(World world, Vec3d pos, UUID entityId) {
        return world.getEntitiesByClass(
                Entity.class,
                Box.of(pos, 1024, 1024, 1024),
                e -> e.getUuid().equals(entityId)
        ).getFirst();
    }

    /**
     * Retrieves a player by their name on the server.
     * @param context Server networkContext
     * @param playerName Player's name
     * @return Server player, if they exist
     */
    public static ServerPlayerEntity getPlayerByName(ServerPacketContext context, String playerName) {
        for (ServerWorld world : context.server().getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.getName().getLiteralString().equals(playerName)) {
                    return player;
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a player by their ID, across all dimensions
     * @param context Server packet context
     * @param playerId Player's id
     * @return Server player, if they exist
     */
    public static ServerPlayerEntity getPlayerById(ServerPacketContext context, UUID playerId) {
        for (ServerWorld world : context.server().getWorlds()) {
            ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUuid(playerId);

            if (player != null) {
                return player;
            }
        }

        // Player not found
        return null;
    }
}
