package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Animal.class)
public interface AnimalAccessor {

	@Accessor("loveCause")
	void biomancy$setLoveCause(UUID loveCause);

}
