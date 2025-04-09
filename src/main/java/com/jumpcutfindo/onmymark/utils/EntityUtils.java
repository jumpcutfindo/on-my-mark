package com.jumpcutfindo.onmymark.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityUtils {
    public static Entity getEntityByUuid(World world, Vec3d pos, UUID entityId) {
        return world.getEntitiesByClass(
                Entity.class,
                Box.of(pos, 1024, 1024, 1024),
                e -> e.getUuid().equals(entityId)
        ).getFirst();
    }
}
