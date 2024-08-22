package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.mixin.accessor.IntegerPropertyAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class EnhancedIntegerProperty {

	private final IntegerProperty property;

	protected EnhancedIntegerProperty(IntegerProperty property) {
		this.property = property;
	}

	public static EnhancedIntegerProperty create(String name, int min, int max) {
		return new EnhancedIntegerProperty(IntegerProperty.create(name, min, max));
	}

	public static EnhancedIntegerProperty wrap(IntegerProperty property) {
		return new EnhancedIntegerProperty(property);
	}

	public IntegerProperty get() {
		return property;
	}

	public Integer getValue(BlockState state) {
		return state.getValue(property);
	}

	public BlockState setValue(BlockState state, int value) {
		int min = getMin();
		int max = getMax();
		return state.setValue(property, Mth.clamp(value, min, max));
	}

	public BlockState addValue(BlockState state, int value) {
		return setValue(state, state.getValue(property) + value);
	}

	public int getMin() {
		return ((IntegerPropertyAccessor) property).biomancy$getMin();
	}

	public int getMax() {
		return ((IntegerPropertyAccessor) property).biomancy$getMax();
	}
}
