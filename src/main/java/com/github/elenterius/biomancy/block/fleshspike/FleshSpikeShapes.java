package com.github.elenterius.biomancy.block.fleshspike;

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

public final class FleshSpikeShapes {

	static final IntermediaryKeyCache<BlockState, ComputedShapes> CACHE = new IntermediaryKeyCache<>(FleshSpikeShapes::computeKey);

	private FleshSpikeShapes() {}

	static void computePossibleShapes(List<BlockState> possibleStates) {
		possibleStates.forEach(possibleState -> CACHE.computeIfAbsent(possibleState, FleshSpikeShapes::computeShapes));
	}

	static VoxelShape getDamageShape(BlockState blockState) {
		return CACHE.get(blockState).damageShape();
	}

	static VoxelShape getBoundingShape(BlockState blockState) {
		return CACHE.get(blockState).boundingShape();
	}

	public static VoxelShape getCollisionShape(BlockState blockState) {
		return CACHE.get(blockState).collisionShape();
	}

	private static Integer computeKey(BlockState blockState) {
		return Objects.hash(blockState.getValue(FleshSpikeBlock.FACING), blockState.getValue(FleshSpikeBlock.SPIKES));
	}

	private static ComputedShapes computeShapes(BlockState blockState) {
		Direction direction = blockState.getValue(FleshSpikeBlock.FACING);
		int spikes = blockState.getValue(FleshSpikeBlock.SPIKES);
		VoxelShape boundingShape = computeBoundingShape(direction, spikes);
		VoxelShape collisionShape = computeCollisionShape(direction, spikes);
		VoxelShape damageShape = computeDamageShape(direction, spikes);
		return new ComputedShapes(boundingShape, collisionShape, damageShape);
	}

	private static VoxelShape computeBoundingShape(Direction direction, int spikes) {
		if (spikes == 1) return VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 7, 10);
		if (spikes == 2)
			return Stream.of(VoxelShapeUtil.createXZRotatedTowards(direction, 9, 0, 8, 13, 7, 12), VoxelShapeUtil.createXZRotatedTowards(direction, 4, 0, 4, 8, 8, 8), VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 4, 12, 1, 11)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		if (spikes == 3)
			return Stream.of(VoxelShapeUtil.createXZRotatedTowards(direction, 1, 0, 9, 5, 6, 13), VoxelShapeUtil.createXZRotatedTowards(direction, 6, 3, 6, 10, 9, 10), VoxelShapeUtil.createXZRotatedTowards(direction, 8, 0, 11, 12, 6, 15), VoxelShapeUtil.createXZRotatedTowards(direction, 10, 0, 3, 14, 8, 7), VoxelShapeUtil.createXZRotatedTowards(direction, 11, 1, 7, 14, 3, 10), VoxelShapeUtil.createXZRotatedTowards(direction, 5, 1, 3, 8, 3, 6), VoxelShapeUtil.createXZRotatedTowards(direction, 3, 0, 2, 12, 1, 12), VoxelShapeUtil.createXZRotatedTowards(direction, 4, 1, 6, 10, 3, 11), VoxelShapeUtil.createXZRotatedTowards(direction, 12, 0, 6, 15, 1, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		return Shapes.empty();
	}

	private static VoxelShape computeCollisionShape(Direction direction, int spikes) {
		if (spikes == 1) return VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 2, 10);
		if (spikes == 2)
			return Stream.of(VoxelShapeUtil.createXZRotatedTowards(direction, 4, 0, 4, 8, 2, 8), VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 4, 12, 1, 11), VoxelShapeUtil.createXZRotatedTowards(direction, 9, 0, 8, 13, 2, 12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		if (spikes == 3)
			return Stream.of(VoxelShapeUtil.createXZRotatedTowards(direction, 3, 0, 2, 12, 1, 12), VoxelShapeUtil.createXZRotatedTowards(direction, 1, 0, 9, 5, 2, 13), VoxelShapeUtil.createXZRotatedTowards(direction, 4, 1, 6, 10, 3, 11), VoxelShapeUtil.createXZRotatedTowards(direction, 10, 0, 3, 14, 2, 7), VoxelShapeUtil.createXZRotatedTowards(direction, 12, 0, 6, 15, 1, 14), VoxelShapeUtil.createXZRotatedTowards(direction, 8, 0, 11, 12, 2, 15)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		return Shapes.empty();
	}

	private static VoxelShape computeDamageShape(Direction direction, int spikes) {
		if (spikes == 1) return VoxelShapeUtil.createXZRotatedTowards(direction, 6, 2, 6, 10, 7, 10);
		if (spikes == 2) return Shapes.join(VoxelShapeUtil.createXZRotatedTowards(direction, 9, 2, 8, 13, 7, 12), VoxelShapeUtil.createXZRotatedTowards(direction, 4, 2, 4, 8, 8, 8), BooleanOp.OR);
		if (spikes == 3)
			return Stream.of(VoxelShapeUtil.createXZRotatedTowards(direction, 1, 2, 9, 5, 6, 13), VoxelShapeUtil.createXZRotatedTowards(direction, 6, 3, 6, 10, 9, 10), VoxelShapeUtil.createXZRotatedTowards(direction, 8, 2, 11, 12, 6, 15), VoxelShapeUtil.createXZRotatedTowards(direction, 10, 2, 3, 14, 8, 7), VoxelShapeUtil.createXZRotatedTowards(direction, 11, 1, 7, 14, 3, 10), VoxelShapeUtil.createXZRotatedTowards(direction, 5, 1, 3, 8, 3, 6)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		return Shapes.empty();
	}

	protected record ComputedShapes(VoxelShape boundingShape, VoxelShape collisionShape, VoxelShape damageShape) {}

}
