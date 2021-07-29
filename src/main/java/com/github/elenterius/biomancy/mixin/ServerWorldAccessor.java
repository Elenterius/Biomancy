package com.github.elenterius.biomancy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {

	@Invoker("getLoadedOrPendingEntity")
	Entity biomancy_getLoadedOrPendingEntity(UUID uuid);

}
