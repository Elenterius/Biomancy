package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class CrystalOre extends OreBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	protected static final VoxelShape SHAPE_UP = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	protected static final VoxelShape SHAPE_DOWN = Block.makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(0.0D, 2.0D, 2.0D, 12.0D, 14.0D, 14.0D);
	protected static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(4.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
	protected static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 12.0D);
	protected static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(2.0D, 2.0D, 4.0D, 14.0D, 14.0D, 16.0D);

	public CrystalOre(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(FACING, Direction.UP));
	}

	@Override
	public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack) {
		super.spawnAdditionalDrops(state, worldIn, pos, stack);
	}

	@Override
	protected int getExperience(Random rand) {
		if (this == ModBlocks.BLIGHT_QUARTZ_ORE.get()) {
			return MathHelper.nextInt(rand, 3, 7);
		}
		return 0;
	}

	@Override
	public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
		return super.getExpDrop(state, reader, pos, fortune, silktouch);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = worldIn.getBlockState(blockPos);
		return blockState.isSolidSide(worldIn, blockPos, direction);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getFace());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
			case UP:
			default:
				return SHAPE_UP;
			case DOWN:
				return SHAPE_DOWN;
			case NORTH:
				return SHAPE_NORTH;
			case SOUTH:
				return SHAPE_SOUTH;
			case WEST:
				return SHAPE_WEST;
			case EAST:
				return SHAPE_EAST;
		}
	}
}
