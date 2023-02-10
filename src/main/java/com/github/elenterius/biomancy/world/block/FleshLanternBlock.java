package com.github.elenterius.biomancy.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FleshLanternBlock extends LanternBlock {

	protected static final VoxelShape SHAPE = Shapes.or(
			Block.box(6, 9, 6, 10, 10, 10),
			Block.box(5, 2, 5, 11, 9, 11),
			Block.box(6, 0, 6, 10, 2, 10)
	);
	protected static final VoxelShape HANGING_SHAPE = Shapes.or(
			Block.box(6, 8, 6, 10, 10, 10),
			Block.box(5, 1, 5, 11, 8, 11),
			Block.box(6, 0, 6, 10, 1, 10)
	);

	public FleshLanternBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Boolean.TRUE.equals(state.getValue(HANGING)) ? HANGING_SHAPE : SHAPE;
	}

}
