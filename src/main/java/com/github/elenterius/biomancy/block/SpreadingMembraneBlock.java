package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.block.cradle.PrimalEnergyHandler;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Random;

public class SpreadingMembraneBlock extends MembraneBlock {

	public SpreadingMembraneBlock(Properties properties, IgnoreEntityCollisionPredicate predicate) {
		super(properties.randomTicks(), predicate);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (level.random.nextFloat() >= 0.5f) return;
		if (!level.isAreaLoaded(pos, 2)) return;

		BlockPos targetPos = pos.relative(Direction.getRandom(random));
		BlockState stateAtTargetPos = level.getBlockState(targetPos);
		if (!stateAtTargetPos.isAir() && !PrimordialEcosystem.isReplaceable(stateAtTargetPos)) return;

		//		boolean hasInvalidNeighborAtTargetPos = Direction.stream()
		//				.map(targetPos::relative)
		//				.filter(neighborPos -> !neighborPos.equals(pos))
		//				.anyMatch(neighborPos -> PrimordialEcosystem.isReplaceable(level.getBlockState(neighborPos)));
		//
		//		if (hasInvalidNeighborAtTargetPos) return;

		if (SpatialShapeManager.getClosestShape(level, pos, MoundShape.class::isInstance) instanceof MoundShape mound) {
			BlockEntity blockEntity = level.getExistingBlockEntity(mound.getOrigin());
			if (blockEntity instanceof PrimalEnergyHandler energyHandler && !mound.hasChamberAt(targetPos)) {

				boolean isNeighborNextToAnyChamber = Arrays.stream(Direction.values()).anyMatch(direction -> mound.hasChamberAt(targetPos.relative(direction)));

				if (isNeighborNextToAnyChamber && energyHandler.drainPrimalEnergy(5) > 0) {
					level.setBlock(targetPos, defaultBlockState(), UPDATE_CLIENTS);
				}
			}
		}
	}

}
