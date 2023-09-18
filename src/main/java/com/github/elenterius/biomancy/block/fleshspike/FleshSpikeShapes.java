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
		return Objects.hash(blockState.getValue(FleshSpikeBlock.FACING), FleshSpikeBlock.getSpikes(blockState));
	}

	private static ComputedShapes computeShapes(BlockState blockState) {
		Direction direction = blockState.getValue(FleshSpikeBlock.FACING);
		int spikes = FleshSpikeBlock.getSpikes(blockState);
		VoxelShape boundingShape = computeBoundingShape(direction, spikes);
		VoxelShape collisionShape = computeCollisionShape(direction, spikes);
		VoxelShape damageShape = computeDamageShape(direction, spikes);
		return new ComputedShapes(boundingShape, collisionShape, damageShape);
	}

	private static VoxelShape computeBoundingShape(Direction direction, int spikes) {
		if (spikes == 1) return VoxelShapeUtil.createXZRotatedTowards(direction, 4, 0, 4, 12, 7, 12);
		if (spikes == 2) return VoxelShapeUtil.createXZRotatedTowards(direction, 2, 0, 2, 14, 8, 14);
		if (spikes == 3) return VoxelShapeUtil.createXZRotatedTowards(direction, 1, 0, 1, 15, 11, 15);
		return Shapes.empty();
	}

	private static VoxelShape computeCollisionShape(Direction direction, int spikes) {
		if (spikes == 1) {
			return VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 2, 10);
		}
		if (spikes == 2) {
			return Stream.of(
					VoxelShapeUtil.createXZRotatedTowards(direction, 4, 0, 6, 8, 2, 10),
					VoxelShapeUtil.createXZRotatedTowards(direction, 8, 0, 7, 12, 2, 11)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		}
		if (spikes == 3) {
			return Stream.of(
					VoxelShapeUtil.createXZRotatedTowards(direction, 3, 0, 2, 7, 2, 6),
					VoxelShapeUtil.createXZRotatedTowards(direction, 5, 0, 5, 11, 4, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 1, 0, 7, 5, 2, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 4, 0, 11, 8, 2, 15),
					VoxelShapeUtil.createXZRotatedTowards(direction, 10, 0, 10, 14, 2, 14)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		}
		return Shapes.empty();
	}

	private static VoxelShape computeDamageShape(Direction direction, int spikes) {
		if (spikes == 1) return VoxelShapeUtil.createXZRotatedTowards(direction, 6, 2, 6, 10, 7, 10);
		if (spikes == 2) {
			return Stream.of(
					VoxelShapeUtil.createXZRotatedTowards(direction, 4, 2, 6, 8, 8, 10),
					VoxelShapeUtil.createXZRotatedTowards(direction, 8, 2, 7, 12, 7, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 7, 0, 4, 10, 2, 7)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		}
		if (spikes == 3) {
			return Stream.of(
					VoxelShapeUtil.createXZRotatedTowards(direction, 3, 2, 2, 7, 6, 6),
					VoxelShapeUtil.createXZRotatedTowards(direction, 11, 2, 2, 15, 8, 6),
					VoxelShapeUtil.createXZRotatedTowards(direction, 5, 4, 5, 11, 11, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 1, 2, 7, 5, 8, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 4, 2, 11, 8, 6, 15),
					VoxelShapeUtil.createXZRotatedTowards(direction, 10, 2, 10, 14, 6, 14)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.empty());
		}
		return Shapes.empty();
	}

	protected record ComputedShapes(VoxelShape boundingShape, VoxelShape collisionShape, VoxelShape damageShape) {}

}
