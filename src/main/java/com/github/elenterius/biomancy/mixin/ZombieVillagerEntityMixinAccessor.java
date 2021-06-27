package com.github.elenterius.biomancy.mixin;

import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ZombieVillagerEntity.class)
public interface ZombieVillagerEntityMixinAccessor {

	@Invoker("cureZombie")
	void biomancy_cureZombie(ServerWorld world);

}
