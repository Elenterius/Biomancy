package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IntegerProperty.class)
public interface IntegerPropertyAccessor {
	@Accessor("min")
	int biomancy$getMin();

	@Accessor("max")
	int biomancy$getMax();
}
