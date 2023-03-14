package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.world.LevelUtil;
import com.github.elenterius.biomancy.world.block.property.BlockPropertyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Set;

public final class PillarPlantUtil {

	private PillarPlantUtil() {}

	public static final Set<Block> PILLAR_PLANTS = Set.of(Blocks.SUGAR_CANE, Blocks.CACTUS);
	public static final PillarPlantHelper CACTUS_HELPER = new PillarPlantHelper(Blocks.CACTUS, 3, CactusBlock.AGE, true);
	public static final PillarPlantHelper SUGAR_CANE_HELPER = new PillarPlantHelper(Blocks.SUGAR_CANE, 3, SugarCaneBlock.AGE);

	public static boolean isPillarPlant(Block block) {
		return PILLAR_PLANTS.contains(block);
	}

	public static boolean applyGrowthBoost(Level level, BlockPos pos, BlockState state, Block block) {
		if (block == SUGAR_CANE_HELPER.block()) {
			return handleGrowth(SUGAR_CANE_HELPER, level, pos, state, SUGAR_CANE_HELPER.defaultHeight, SUGAR_CANE_HELPER.maxAge);
		}
		else if (block == CACTUS_HELPER.block()) {
			return handleGrowth(CACTUS_HELPER, level, pos, state, CACTUS_HELPER.defaultHeight, CACTUS_HELPER.maxAge);
		}
		return false;
	}

	public static boolean applyMegaGrowthBoost(Level level, BlockPos pos, BlockState state, Block block) {
		if (block == SUGAR_CANE_HELPER.block()) {
			return handleMegaGrowth(SUGAR_CANE_HELPER, level, pos, state, SUGAR_CANE_HELPER.defaultHeight * 3, SUGAR_CANE_HELPER.maxAge);
		}
		else if (block == CACTUS_HELPER.block()) {
			return handleMegaGrowth(CACTUS_HELPER, level, pos, state, CACTUS_HELPER.defaultHeight * 3, CACTUS_HELPER.maxAge);
		}
		return false;
	}

	public static boolean handleGrowth(PillarPlantHelper plantHelper, Level level, BlockPos pos, BlockState state) {
		return handleGrowth(plantHelper, level, pos, state, plantHelper.defaultHeight, level.random.nextInt(3) + 2);
	}

	private static boolean handleGrowth(PillarPlantHelper plantHelper, Level level, BlockPos pos, BlockState state, int maxHeight, int ageModifier) {
		if (plantHelper.canGrow(level, pos, state, maxHeight)) {
			if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
				plantHelper.grow(serverLevel, pos, state, maxHeight, ageModifier);
				serverLevel.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
			}
			return true;
		}
		return false;
	}

	private static boolean handleMegaGrowth(PillarPlantHelper plantHelper, Level level, BlockPos pos, BlockState state, int maxHeight, int ageModifier) {
		boolean hasGrown = false;

		for (int i = 0; i < 3; i++) {
			if (state.isAir() || !plantHelper.canGrow(level, pos, state, maxHeight)) break;

			if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
				plantHelper.grow(serverLevel, pos, state, maxHeight, ageModifier);
				serverLevel.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
			}
			pos = pos.above();
			state = level.getBlockState(pos);
			hasGrown = true;
		}

		return hasGrown;
	}

	public record PillarPlantHelper(Block block, int defaultHeight, IntegerProperty ageProperty, int maxAge, boolean callNeighborChanged) {

		public PillarPlantHelper(Block block, int defaultHeight, IntegerProperty ageProperty) {
			this(block, defaultHeight, ageProperty, BlockPropertyUtil.getMaxAge(ageProperty), false);
		}

		public PillarPlantHelper(Block block, int defaultHeight, IntegerProperty ageProperty, boolean callNeighborChanged) {
			this(block, defaultHeight, ageProperty, BlockPropertyUtil.getMaxAge(ageProperty), callNeighborChanged);
		}

		public boolean isNotFullyGrown(BlockState state) {
			return state.getValue(ageProperty) < maxAge;
		}

		public boolean canGrow(BlockGetter level, BlockPos pos, BlockState state, int maxHeight) {
			if (state.getBlock() != block) return false;

			BlockState stateAbove = level.getBlockState(pos.above());
			if (stateAbove.isAir()) {
				int n = LevelUtil.getNumOfBlocksBelow(level, pos, block, maxHeight);
				return n + 1 < maxHeight && isNotFullyGrown(level.getBlockState(pos));
			}
			else {
				if (stateAbove.getBlock() == block) {
					int nUp = LevelUtil.getNumOfBlocksAbove(level, pos, block, maxHeight);
					int nDown = LevelUtil.getNumOfBlocksBelow(level, pos, block, maxHeight);
					return nUp + nDown + 1 < maxHeight && isNotFullyGrown(level.getBlockState(pos.above(nUp)));
				}
			}
			return false;
		}

		public void grow(ServerLevel level, BlockPos pos, BlockState state, int maxHeight, int ageModifier) {
			BlockState stateAbove = level.getBlockState(pos.above());
			if (stateAbove.isAir()) {
				grow(level, pos, state, ageModifier);
			}
			else {
				int nUp = LevelUtil.getNumOfBlocksAbove(level, pos, block, maxHeight);
				BlockPos posTop = pos.above(nUp);
				grow(level, posTop, level.getBlockState(posTop), ageModifier);
			}
		}

		private void grow(ServerLevel level, BlockPos pos, BlockState state, int ageModifier) {
			int age = Math.min(maxAge, state.getValue(ageProperty) + ageModifier);
			if (age == maxAge) {
				BlockPos posAbove = pos.above();
				level.setBlockAndUpdate(posAbove, block.defaultBlockState());
				BlockState newState = state.setValue(ageProperty, 0);
				level.setBlock(pos, newState, Block.UPDATE_INVISIBLE);
				if (callNeighborChanged) newState.neighborChanged(level, posAbove, block, pos, false);
			}
			else {
				level.setBlock(pos, state.setValue(ageProperty, age), Block.UPDATE_INVISIBLE);
			}
		}

	}

}
