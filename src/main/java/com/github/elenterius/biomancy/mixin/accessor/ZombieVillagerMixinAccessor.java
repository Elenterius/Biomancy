package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ZombieVillager.class)
public interface ZombieVillagerMixinAccessor {

	@Invoker("finishConversion")
	void biomancy$cureZombie(ServerLevel level);

}
