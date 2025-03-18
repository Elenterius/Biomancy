package com.github.elenterius.biomancy.block.mound;

import com.github.elenterius.biomancy.block.cradle.PrimalEnergyHandler;
import com.github.elenterius.biomancy.block.membrane.IgnoreEntityCollisionPredicate;
import com.github.elenterius.biomancy.block.membrane.MembraneBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.util.VectorUtil;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PrimalMembraneBlock extends MembraneBlock {

	public PrimalMembraneBlock(Properties properties, IgnoreEntityCollisionPredicate predicate) {
		super(properties.randomTicks(), predicate);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.random.nextFloat() >= 0.5f) return;
		if (!level.isAreaLoaded(pos, 2)) return;

		BlockPos targetPos = pos.offset(VectorUtil.randomOffsetInCube3i(random));
		BlockState stateAtTargetPos = level.getBlockState(targetPos);

		if (!stateAtTargetPos.isAir() && !(stateAtTargetPos.getBlock() instanceof MalignantVeinsBlock) && !PrimordialEcosystem.isReplaceable(stateAtTargetPos)) return;

		if (SpatialShapeManager.getClosestShape(level, pos, MoundShape.class::isInstance) instanceof MoundShape mound) {
			BlockEntity blockEntity = level.getExistingBlockEntity(mound.getOrigin());
			if (blockEntity instanceof PrimalEnergyHandler energyHandler && !mound.hasChamberAt(targetPos)) {

				int nextToAnyChamberCount = 0;
				for (Direction direction : Direction.allShuffled(random)) {
					if (mound.hasChamberAt(targetPos.relative(direction))) {
						nextToAnyChamberCount++;
						if (nextToAnyChamberCount > 1) break;
					}
				}

				if (energyHandler.drainPrimalEnergy(4) > 0) {
					if (nextToAnyChamberCount > 1) {
						level.setBlock(targetPos, defaultBlockState(), UPDATE_CLIENTS);
					}
					else {
						level.setBlock(targetPos, MalignantFleshBlock.CHARGE.setValue(ModBlocks.SPREADING_MALIGNANT_FLESH.get().defaultBlockState(), energyHandler.drainPrimalEnergy(MalignantFleshBlock.CHARGE.getMax())), UPDATE_CLIENTS);
					}
				}
			}
		}
	}

}
