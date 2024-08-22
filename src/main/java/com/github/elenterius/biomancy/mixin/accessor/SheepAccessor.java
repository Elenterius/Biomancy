package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Sheep.class)
public interface SheepAccessor {

	@Accessor("eatAnimationTick")
	int biomancy$getEatAnimationTick();

}
