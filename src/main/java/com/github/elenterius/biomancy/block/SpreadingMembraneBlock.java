package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.block.cradle.PrimalEnergyHandler;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.util.ArrayUtil;
import com.github.elenterius.biomancy.util.VectorUtil;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class SpreadingMembraneBlock extends MembraneBlock {

	public SpreadingMembraneBlock(Properties properties, IgnoreEntityCollisionPredicate predicate) {
		super(properties.randomTicks(), predicate);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (level.random.nextFloat() >= 0.5f) return;
		if (!level.isAreaLoaded(pos, 2)) return;

		BlockPos targetPos = pos.offset(VectorUtil.randomOffsetInCube3i(random));
		BlockState stateAtTargetPos = level.getBlockState(targetPos);

		if (!stateAtTargetPos.isAir() && !(stateAtTargetPos.getBlock() instanceof FleshVeinsBlock) && !PrimordialEcosystem.isReplaceable(stateAtTargetPos)) return;

		if (SpatialShapeManager.getClosestShape(level, pos, MoundShape.class::isInstance) instanceof MoundShape mound) {
			BlockEntity blockEntity = level.getExistingBlockEntity(mound.getOrigin());
			if (blockEntity instanceof PrimalEnergyHandler energyHandler && !mound.hasChamberAt(targetPos)) {

				int nextToAnyChamberCount = 0;
				for (Direction direction : ArrayUtil.shuffleCopy(Direction.values(), random)) {
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
						level.setBlock(targetPos, ModBlocks.MALIGNANT_FLESH.get().defaultBlockState(), UPDATE_CLIENTS);
						PrimordialEcosystem.spreadMalignantVeinsFromSource(level, targetPos, PrimordialEcosystem.MAX_CHARGE_SUPPLIER);
					}
				}
			}
		}
	}

}
