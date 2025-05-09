package com.jumpcutfindo.onmymark.server.utils;

import com.jumpcutfindo.onmymark.server.network.ServerPacketContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class ServerEntityUtils {
    /**
     * Retrieves a player by their name on the server.
     * @param context Server networkContext
     * @param playerName Player's name
     * @return Server player, if they exist
     */
    public static ServerPlayerEntity getPlayerByName(ServerPacketContext context, String playerName) {
        for (ServerWorld world : context.server().getWorlds()) {

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.getName().getLiteralString().toLowerCase().equals(playerName.toLowerCase())) {
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
