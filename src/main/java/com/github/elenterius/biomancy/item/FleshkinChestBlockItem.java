package com.github.elenterius.biomancy.item;

import net.minecraft.world.level.block.Block;

public class FleshkinChestBlockItem extends SimpleBlockItem {

	public FleshkinChestBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return true;
	}

}
