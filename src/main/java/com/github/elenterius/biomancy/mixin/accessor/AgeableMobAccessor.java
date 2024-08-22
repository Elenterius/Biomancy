package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableMob.class)
public interface AgeableMobAccessor {

	@Accessor("forcedAge")
	int biomancy$getForcedAge();

	@Accessor("forcedAge")
	void biomancy$setForcedAge(int age);

}
