package com.github.elenterius.biomancy.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IHighlightRayTraceResultItem {
	float DEFAULT_MAX_DISTANCE = 20f;

	default double getMaxRayTraceDistance() {
		return DEFAULT_MAX_DISTANCE;
	}

	@OnlyIn(Dist.CLIENT)
	default boolean canHighlightLivingEntities(ItemStack stack) {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	default boolean canHighlightBlocks(ItemStack stack) {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	default int getColorForEnemyEntity(ItemStack stack, Entity entity) {
		return 0xCE0018;
	}

	@OnlyIn(Dist.CLIENT)
	default int getColorForFriendlyEntity(ItemStack stack, Entity entity) {
		return 0x00ff00;
	}

	@OnlyIn(Dist.CLIENT)
	default int getColorForBlock(ItemStack stack, BlockPos pos) {
		return 0x00ff00;
	}
}
