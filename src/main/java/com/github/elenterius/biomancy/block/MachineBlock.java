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

public abstract class MachineBlock<T extends MachineTileEntity<?, ?>> extends OwnableContainerBlock {

	public static final BooleanProperty CRAFTING = ModBlocks.CRAFTING_PROPERTY;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected MachineBlock(Properties builder, boolean noDefaultSate) {
		super(builder);
	}

	protected MachineBlock(Properties builder) {
		super(builder);
		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(CRAFTING, false).setValue(HORIZONTAL_FACING, Direction.NORTH));
	}

	@Nullable
	@Override
	public abstract T newBlockEntity(IBlockReader worldIn);

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POWERED, CRAFTING, HORIZONTAL_FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isClientSide()) return ActionResultType.SUCCESS;

		//TODO: verify that authorization works
		INamedContainerProvider containerProvider = getMenuProvider(state, worldIn, pos);
		if (containerProvider != null && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

	protected int getRedstoneLevel(BlockState state) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	protected int getPoweredDuration() {
		return 2;
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (state.getValue(POWERED)) { //after pending block update deactivate redstone
			worldIn.setBlock(pos, state.setValue(POWERED, Boolean.FALSE), Constants.BlockFlags.DEFAULT);
			updateNeighbors(worldIn, pos);
		}
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!isMoving && !state.is(newState.getBlock())) {
			if (state.getValue(POWERED)) {
				updateNeighbors(worldIn, pos);
			}
			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof MachineTileEntity<?, ?>) {
				((MachineTileEntity<?, ?>) tileEntity).dropAllInvContents(worldIn, pos);
			}
			if (state.getValue(CRAFTING)) {
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	public void powerBlock(World worldIn, BlockPos pos, BlockState state) {
		worldIn.setBlock(pos, state.setValue(POWERED, Boolean.TRUE), Constants.BlockFlags.DEFAULT);
		updateNeighbors(worldIn, pos);
		worldIn.getBlockTicks().scheduleTick(pos, this, getPoweredDuration());
	}

	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getRedstoneLevel(blockState);
	}

	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return getRedstoneLevel(blockState);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		return blockState.getValue(CRAFTING) ? 15 : 0;
	}

	protected void updateNeighbors(World worldIn, BlockPos pos) {
		worldIn.updateNeighborsAt(pos, this);
//		worldIn.notifyNeighborsOfStateChange(pos.down(), this);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
	}
}
