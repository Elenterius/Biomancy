package com.github.elenterius.biomancy.block.bloom;

import com.github.elenterius.biomancy.util.IntermediaryKeyCache;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Objects;

public final class BloomShapes {

	static final IntermediaryKeyCache<BlockState, ComputedShapes> CACHE = new IntermediaryKeyCache<>(BloomShapes::computeKey);

	private BloomShapes() {}

	static void computePossibleShapes(List<BlockState> possibleStates) {
		possibleStates.forEach(possibleState -> CACHE.computeIfAbsent(possibleState, BloomShapes::computeShapes));
	}

	static VoxelShape getBoundingShape(BlockState blockState) {
		return CACHE.get(blockState).boundingShape();
	}

	static VoxelShape getCollisionShape(BlockState blockState) {
		return CACHE.get(blockState).collisionShape();
	}

	private static Integer computeKey(BlockState blockState) {
		Direction direction = BloomBlock.getFacing(blockState);
		int stage = BloomBlock.getStage(blockState);

		return Objects.hash(direction, stage);
	}

	private static ComputedShapes computeShapes(BlockState blockState) {
		Direction direction = BloomBlock.getFacing(blockState);

		int stage = BloomBlock.getStage(blockState);

		if (stage == 0) {
			VoxelShape boundingShape = VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 5, 11, 6, 11);
			VoxelShape collisionShape = VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 5, 11, 2, 11);
			return new ComputedShapes(boundingShape, collisionShape);
		}

		VoxelShape shape = switch (stage) {
			case 1 -> Shapes.join(
					VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 5, 11, 2, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 4, 2, 4, 12, 10, 12), BooleanOp.OR);
			case 2 -> Shapes.join(
					VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 5, 11, 2, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 2, 2, 2, 14, 15, 14), BooleanOp.OR);
			case 3, 4 -> Shapes.block();
			default -> Shapes.empty();
		};

		return new ComputedShapes(shape, shape);
	}

	protected record ComputedShapes(VoxelShape boundingShape, VoxelShape collisionShape) {}
}
