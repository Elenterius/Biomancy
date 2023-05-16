package com.github.elenterius.biomancy.block.mawhopper;

import com.github.elenterius.biomancy.util.IntermediaryKeyCache;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class MawHopperShapes {

	private static final IntermediaryKeyCache<BlockState, VoxelShape> CACHE = new IntermediaryKeyCache<>(MawHopperShapes::computeKey);

	private MawHopperShapes() {}

	static void computePossibleShapes(List<BlockState> possibleStates) {
		possibleStates.forEach(possibleState -> CACHE.computeIfAbsent(possibleState, MawHopperShapes::computeShape));
	}

	private static Integer computeKey(BlockState blockState) {
		return Objects.hash(MawHopperBlock.getDirection(blockState), MawHopperBlock.getType(blockState));
	}

	private static VoxelShape computeShape(BlockState blockState) {
		Direction direction = MawHopperBlock.getDirection(blockState);
		MawHopperBlock.Type type = MawHopperBlock.getType(blockState);
		return computeVoxelShape(direction, type);
	}

	private static VoxelShape computeVoxelShape(Direction direction, MawHopperBlock.Type type) {
		if (type == MawHopperBlock.Type.INPUT) {
			return Stream.of(
					VoxelShapeUtil.createXZRotatedTowards(direction, 0, 13, 13, 16, 16, 16),
					VoxelShapeUtil.createXZRotatedTowards(direction, 0, 13, 0, 16, 16, 3),
					VoxelShapeUtil.createXZRotatedTowards(direction, 13, 13, 3, 16, 16, 13),
					VoxelShapeUtil.createXZRotatedTowards(direction, 0, 13, 3, 3, 16, 13),
					VoxelShapeUtil.createXZRotatedTowards(direction, 3, 10, 3, 13, 13, 13),
					VoxelShapeUtil.createXZRotatedTowards(direction, 5, 6, 5, 11, 10, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 6, 10)
			).reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR)).orElse(Shapes.block());
		}

		//connected shape
		return Stream.of(
				VoxelShapeUtil.createXZRotatedTowards(direction, 5, 12, 5, 11, 16, 11),
				VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 12, 10)
		).reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR)).orElse(Shapes.block());
	}

	static VoxelShape getShape(BlockState blockState) {
		return CACHE.get(blockState);
	}
}
