package com.github.elenterius.biomancy.mixin;

import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Slime.class)
public interface SlimeAccessor {

	@Invoker("setSize")
	void biomancy_setSlimeSize(int size, boolean resetHealth);

}
