package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.FleshChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class FleshChestBlock extends OwnableContainerBlock {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(1d, 0d, 1d, 15d, 14d, 15d);

	public FleshChestBlock(Properties builder) {
		super(builder);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote()) return ActionResultType.SUCCESS;

		INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
		if (containerProvider != null && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof FleshChestTileEntity) {
			return Container.calcRedstoneFromInventory(((FleshChestTileEntity) tileEntity).getInventory());
		}
		return 0;
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
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new FleshChestTileEntity();
	}

}
