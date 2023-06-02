package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public final class LevelUtil {

	private LevelUtil() {}

	public static LazyOptional<IItemHandler> getItemHandler(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		BlockState state = level.getBlockState(pos);
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity != null) {
				return blockEntity.getCapability(ModCapabilities.ITEM_HANDLER, direction);
			}
		}
		return LazyOptional.empty();
	}

	public static int getNumOfBlocksAbove(BlockGetter level, BlockPos pos, Block targetBlock, int maxHeight) {
		int i = 0;
		while (i < maxHeight && level.getBlockState(pos.above(i + 1)).getBlock() == targetBlock) i++;
		return i;
	}

	public static int getNumOfBlocksBelow(BlockGetter level, BlockPos pos, Block targetBlock, int maxHeight) {
		int i = 0;
		while (i < maxHeight && level.getBlockState(pos.below(i + 1)).getBlock() == targetBlock) i++;
		return i;
	}

}
