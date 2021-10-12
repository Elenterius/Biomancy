package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.util.GeometricShape;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IAreaHarvestingItem {

	byte getBlockHarvestRange(ItemStack stack);

	default GeometricShape getHarvestShape(ItemStack stack) {
		return GeometricShape.PLANE;
	}

	default boolean isAreaSelectionVisibleFor(ItemStack stack, BlockPos pos, BlockState state) {
		return true;
	}
}
