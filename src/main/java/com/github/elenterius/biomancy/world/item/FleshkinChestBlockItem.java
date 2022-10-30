package com.github.elenterius.biomancy.world.item;

import net.minecraft.world.level.block.Block;

public class FleshkinChestBlockItem extends SimpleBlockItem {

	public FleshkinChestBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	//	public boolean canFitInsideContainerItems(ItemStack stack) {
	//		if (!stack.isEmpty()) {
	//			CompoundTag nbt = stack.getTagElement("BlockEntityTag");
	//			if (nbt == null) return true;
	//			return nbt.getCompound("Inventory").isEmpty();
	//		}
	//		return false;
	//	}

}
