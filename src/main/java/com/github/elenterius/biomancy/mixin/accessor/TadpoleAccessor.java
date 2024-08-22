package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.animal.frog.Tadpole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Tadpole.class)
public interface TadpoleAccessor {
	@Invoker("ageUp")
	void biomancy$AgeUp();
}
