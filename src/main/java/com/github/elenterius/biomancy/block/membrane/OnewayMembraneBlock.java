package com.github.elenterius.biomancy.block.membrane;

import com.github.elenterius.biomancy.block.base.FacingBlock;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OnewayMembraneBlock extends FacingBlock implements Membrane {

	public static final VoxelShape SOLID_SHAPE_UP = createShape(Direction.UP);
	public static final VoxelShape SOLID_SHAPE_DOWN = createShape(Direction.DOWN);
	public static final VoxelShape SOLID_SHAPE_NORTH = createShape(Direction.NORTH);
	public static final VoxelShape SOLID_SHAPE_SOUTH = createShape(Direction.SOUTH);
	public static final VoxelShape SOLID_SHAPE_WEST = createShape(Direction.WEST);
	public static final VoxelShape SOLID_SHAPE_EAST = createShape(Direction.EAST);

	public OnewayMembraneBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	private static VoxelShape createShape(Direction facing) {
		return VoxelShapeUtil.createXZRotatedTowards(facing, 0, 12, 0, 16, 16, 16);
	}

	protected static VoxelShape getShape(Direction facing) {
		return switch (facing) {
			case UP -> SOLID_SHAPE_UP;
			case DOWN -> SOLID_SHAPE_DOWN;
			case NORTH -> SOLID_SHAPE_NORTH;
			case SOUTH -> SOLID_SHAPE_SOUTH;
			case WEST -> SOLID_SHAPE_WEST;
			case EAST -> SOLID_SHAPE_EAST;
		};
	}

	@Override
	public boolean shouldIgnoreEntityCollisionAt(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
		return false;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

		if (context instanceof EntityCollisionContext entityContext) {
			Entity entity = entityContext.getEntity();
			if (entity != null) {

				Direction facing = getFacing(state);
				Direction.Axis axis = facing.getAxis();
				AABB entityAABB = entity.getBoundingBox();

				boolean isAboveFace = switch (facing.getAxisDirection()) {
					case POSITIVE -> entityAABB.min(axis) > pos.get(axis) + 1d - (double) 1e-5f;
					case NEGATIVE -> entityAABB.max(axis) < pos.get(axis) + (double) 1e-5f;
				};

				if (isAboveFace && !context.isDescending()) {
					return getShape(facing);
				}
				else {
					return Shapes.empty();
				}
			}
		}

		return Shapes.block();
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		Membrane.setPlayerIsInsideMembrane(pos, entity);
	}

	@Override
	public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
		return adjacentState.is(this) && getFacing(state) == getFacing(adjacentState);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return getFacing(state).getAxis().isHorizontal();
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1f;
	}

}
