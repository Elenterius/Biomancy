package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.util.GeometricShape;
import net.minecraft.item.ItemStack;

public interface IAreaHarvestingItem {

	byte getBlockHarvestRange(ItemStack stack);

	default GeometricShape getHarvestShape(ItemStack stack) {
		return GeometricShape.PLANE;
	}

}
