package com.github.elenterius.biomancy.block.membrane;

import com.github.elenterius.biomancy.entity.misc.BiomancyPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface Membrane {

	boolean shouldIgnoreEntityCollisionAt(BlockState state, BlockGetter level, BlockPos pos, Entity entity);

	static void setPlayerIsInsideMembrane(BlockPos pos, Entity entity) {
		if (entity instanceof BiomancyPlayer bioPlayer && Mth.floor(entity.getEyeY()) == pos.getY()) {
			bioPlayer.biomancy$setIsInsideMembrane(true);
		}
	}

}
