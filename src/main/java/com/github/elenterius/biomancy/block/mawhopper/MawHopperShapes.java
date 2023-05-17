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
		return Objects.hash(MawHopperBlock.getConnection(blockState), MawHopperBlock.getVertexType(blockState));
	}

	private static VoxelShape computeShape(BlockState blockState) {
		DirectedConnection connection = MawHopperBlock.getConnection(blockState);
		VertexType vertexType = MawHopperBlock.getVertexType(blockState);
		return computeVoxelShape(connection, vertexType);
	}

	private static VoxelShape computeVoxelShape(DirectedConnection connection, VertexType vertexType) {
		if (vertexType == VertexType.SOURCE) {
			Direction direction = connection.ingoing;
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

		if (connection.isStraight()) {
			//connected straight shape
			Direction direction = connection.ingoing;
			return Stream.of(
					VoxelShapeUtil.createXZRotatedTowards(direction, 5, 12, 5, 11, 16, 11),
					VoxelShapeUtil.createXZRotatedTowards(direction, 6, 0, 6, 10, 12, 10)
			).reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR)).orElse(Shapes.block());
		}

		//connected corner shape
		VoxelShape headShape = Shapes.join(
				VoxelShapeUtil.createXZRotatedTowards(connection.ingoing, 5, 12, 5, 11, 16, 11),
				VoxelShapeUtil.createXZRotatedTowards(connection.ingoing, 6, 6, 6, 10, 12, 10), BooleanOp.OR);
		VoxelShape tailShape = VoxelShapeUtil.createXZRotatedTowards(connection.outgoing.getOpposite(), 6, 0, 6, 10, 6, 10);
		return Shapes.join(headShape, tailShape, BooleanOp.OR);
	}

	static VoxelShape getShape(BlockState blockState) {
		return CACHE.get(blockState);
	}
}
