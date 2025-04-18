package com.jumpcutfindo.onmymark.mixin;

import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VehicleEntity.class)
public interface VehicleEntityMixin {
    @Invoker("asItem")
    Item asItem();
}
