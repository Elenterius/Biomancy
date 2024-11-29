package com.github.elenterius.biomancy.integration.overweightfarming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface OverweightFarmingHelper {

	boolean canGrowOverweight(Level level, BlockPos pos, BlockState state);

	void growOverweight(ServerLevel level, BlockPos pos, BlockState state, RandomSource random);

	static OverweightFarmingHelper createEmpty() {
		return new OverweightFarmingHelper() {
			@Override
			public boolean canGrowOverweight(Level level, BlockPos pos, BlockState state) {
				return false;
			}

			@Override
			public void growOverweight(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {}
		};
	}

}
