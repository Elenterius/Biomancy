package com.github.elenterius.biomancy.block.base;

import com.github.elenterius.biomancy.init.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class MachineBlock extends BaseEntityBlock {

	public static final BooleanProperty CRAFTING = ModBlockProperties.IS_CRAFTING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	protected MachineBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(CRAFTING, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED, CRAFTING);
	}

	protected int getRedstoneLevel(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(POWERED)) ? 15 : 0;
	}

	protected int getPoweredDuration() {
		return 2;
	}

	protected void updateNeighbors(Level level, BlockPos pos) {
		level.updateNeighborsAt(pos, this);
//		level.notifyNeighborsOfStateChange(pos.down(), this);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (Boolean.TRUE.equals(state.getValue(POWERED))) { //after pending block update deactivate red-stone
			level.setBlock(pos, state.setValue(POWERED, Boolean.FALSE), Block.UPDATE_ALL);
			updateNeighbors(level, pos);
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (Boolean.TRUE.equals(state.getValue(POWERED))) {
				updateNeighbors(level, pos);
			}
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof MachineBlockEntity<?, ?, ?> machine) {
				machine.dropAllInvContents(level, pos);
			}
			if (Boolean.TRUE.equals(state.getValue(CRAFTING))) {
				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public void powerBlock(Level level, BlockPos pos, BlockState state) {
		level.setBlock(pos, state.setValue(POWERED, Boolean.TRUE), Block.UPDATE_ALL);
		updateNeighbors(level, pos);
		level.scheduleTick(pos, this, getPoweredDuration());
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getRedstoneLevel(state);
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getRedstoneLevel(state);
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
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return Boolean.TRUE.equals(state.getValue(CRAFTING)) ? 15 : 0;
	}

}
