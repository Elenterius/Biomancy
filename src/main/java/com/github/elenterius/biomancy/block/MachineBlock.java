package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.tileentity.MachineTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class MachineBlock<T extends MachineTileEntity<?,?>> extends OwnableContainerBlock {

	public static final BooleanProperty CRAFTING = ModBlocks.CRAFTING_PROPERTY;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected MachineBlock(Properties builder) {
		super(builder);
		setDefaultState(stateContainer.getBaseState().with(POWERED, false).with(CRAFTING, false).with(FACING, Direction.NORTH));
	}

	@Nullable
	@Override
	public abstract T createNewTileEntity(IBlockReader worldIn);

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POWERED, CRAFTING, FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote()) return ActionResultType.SUCCESS;

		//TODO: verify that authorization works
		INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
		if (containerProvider != null && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

	protected int getRedstoneLevel(BlockState state) {
		return state.get(POWERED) ? 15 : 0;
	}

	protected int getPoweredDuration() {
		return 20;
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (state.get(POWERED)) { //after pending block update deactivate redstone
			worldIn.setBlockState(pos, state.with(POWERED, Boolean.FALSE), Constants.BlockFlags.DEFAULT);
			updateNeighbors(worldIn, pos);
		}
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!isMoving && !state.matchesBlock(newState.getBlock())) {
			if (state.get(POWERED)) {
				updateNeighbors(worldIn, pos);
			}
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof MachineTileEntity<?,?>) {
				((MachineTileEntity<?,?>) tileEntity).dropAllInvContents(worldIn, pos);
			}
			if (state.get(CRAFTING)) {
				worldIn.updateComparatorOutputLevel(pos, this);
			}
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	public void powerBlock(World worldIn, BlockPos pos, BlockState state) {
		worldIn.setBlockState(pos, state.with(POWERED, Boolean.TRUE), Constants.BlockFlags.DEFAULT);
		updateNeighbors(worldIn, pos);
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, getPoweredDuration());
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getRedstoneLevel(blockState);
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getRedstoneLevel(blockState);
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return blockState.get(CRAFTING) ? 15 : 0;
	}

	protected void updateNeighbors(World worldIn, BlockPos pos) {
		worldIn.notifyNeighborsOfStateChange(pos, this);
//		worldIn.notifyNeighborsOfStateChange(pos.down(), this);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
}
