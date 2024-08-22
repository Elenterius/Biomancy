package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Creeper.class)
public interface CreeperAccessor {

	@Invoker("explodeCreeper")
	void biomancy$explodeCreeper();

}
