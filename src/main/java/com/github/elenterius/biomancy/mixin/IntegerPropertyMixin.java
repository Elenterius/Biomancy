package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.util.MinMaxIntegerProvider;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(IntegerProperty.class)
public class IntegerPropertyMixin implements MinMaxIntegerProvider {

	@Shadow
	@Final
	private ImmutableSet<Integer> values;

	@Unique
	private MinMaxIntegerProvider biomancy$minMaxProvider = null;

	@Unique
	private MinMaxIntegerProvider biomancy$getMinMaxProvider() {
		if (biomancy$minMaxProvider == null) {
			biomancy$minMaxProvider = MinMaxIntegerProvider.of(values);
		}
		return biomancy$minMaxProvider;
	}

	@Override
	public int biomancy$getMin() {
		return biomancy$getMinMaxProvider().biomancy$getMin();
	}

	@Override
	public int biomancy$getMax() {
		return biomancy$getMinMaxProvider().biomancy$getMax();
	}

}
