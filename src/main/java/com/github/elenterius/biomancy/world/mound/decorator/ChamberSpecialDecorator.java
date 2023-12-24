package com.github.elenterius.biomancy.world.mound.decorator;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.util.LevelUtil;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.mound.Chamber;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ChamberSpecialDecorator {

	boolean canDecorate(Chamber chamber, Level level, BlockPos pos, Direction axisDirection, BlockPos closeOffsetPos, BlockState closeOffsetState, BlockPos farOffsetPos, BlockState farOffsetState);

	boolean decorate(Chamber chamber, Level level, BlockPos pos, Direction axisDirection, BlockPos closeOffsetPos, BlockState closeOffsetState, BlockPos farOffsetPos, BlockState farOffsetState);

	ChamberSpecialDecorator BLOOMLIGHT = new ChamberSpecialDecorator() {
		@Override
		public boolean canDecorate(Chamber chamber, Level level, BlockPos pos, Direction axisDirection, BlockPos closeOffsetPos, BlockState closeOffsetState, BlockPos farOffsetPos, BlockState farOffsetState) {
			boolean isFleshBlock = PrimordialEcosystem.FULL_FLESH_BLOCKS.contains(farOffsetState.getBlock());
			return isFleshBlock && axisDirection != Direction.DOWN && LevelUtil.getMaxBrightness(level, pos) < 5;
		}

		@Override
		public boolean decorate(Chamber chamber, Level level, BlockPos pos, Direction axisDirection, BlockPos closeOffsetPos, BlockState closeOffsetState, BlockPos farOffsetPos, BlockState farOffsetState) {
			return level.setBlock(closeOffsetPos, ModBlocks.BLOOMLIGHT.get().defaultBlockState(), Block.UPDATE_CLIENTS);
		}
	};
}
