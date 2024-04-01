package com.github.elenterius.biomancy.block.modularlarynx;

import com.github.elenterius.biomancy.block.property.BlockPropertyUtil;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ModularLarynxBlock extends BaseEntityBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

	protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

	public ModularLarynxBlock(Properties properties) {
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(NOTE, 0).setValue(POWERED, Boolean.FALSE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED, NOTE);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.MODULAR_LARYNX.get().create(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		ItemStack heldStack = player.getItemInHand(hand);
		if (!heldStack.isEmpty()) {
			if (level.getBlockEntity(pos) instanceof ModularLarynxBlockEntity larynx) {
				ItemStack remainder = larynx.insertItemStack(heldStack.copy());
				if (remainder.getCount() != heldStack.getCount()) {
					if (!player.isCreative()) {
						player.setItemInHand(hand, remainder);
					}
					return InteractionResult.CONSUME;
				}
			}
		}
		else if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof ModularLarynxBlockEntity larynx) {
			if (!larynx.isInventoryEmpty()) {
				larynx.dropInventoryContents(level, pos);
				return InteractionResult.CONSUME;
			}
		}

		state = !player.isShiftKeyDown() ? state.cycle(NOTE) : state.setValue(NOTE, BlockPropertyUtil.getPrevious(NOTE, state.getValue(NOTE)));
		level.setBlock(pos, state, Block.UPDATE_ALL);
		playSound(state, level, pos);

		return InteractionResult.CONSUME;
	}

	@Override
	public void attack(BlockState state, Level level, BlockPos pos, Player player) {
		playSound(state, level, pos);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		boolean hasSignal = level.hasNeighborSignal(pos);
		if (hasSignal != Boolean.TRUE.equals(state.getValue(POWERED))) {
			if (hasSignal) playSound(state, level, pos);
			level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_ALL);
		}
	}

	private void playSound(BlockState state, Level level, BlockPos pos) {
		if (level.isClientSide) return;
		if (!level.isEmptyBlock(pos.above())) return;

		if (level.getBlockEntity(pos) instanceof ModularLarynxBlockEntity larynx) {
			int note = state.getValue(NOTE);
			float pitch = (float) Math.pow(2d, (note - 12) / 12d);
			larynx.playSound(3f, pitch);
			level.blockEvent(pos, this, 0, note);
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		if (id == 0) {
			level.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5d, pos.getY() + 1.2d, pos.getZ() + 0.5d, param / 24d, 0d, 0d);
		}
		return true;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity tileEntity = level.getBlockEntity(pos);
			if (tileEntity instanceof ModularLarynxBlockEntity voiceBox) {
				voiceBox.dropInventoryContents(level, pos);
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

}
