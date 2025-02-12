package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {

	@Accessor("random")
	RandomSource biomancy$random();

	@Accessor("dimensions")
	void biomancy$setDimensions(EntityDimensions dimensions);

	@Accessor("dimensions")
	EntityDimensions biomancy$getDimensions();

	@Accessor("eyeHeight")
	void biomancy$setEyeHeight(float eyeHeight);

}
