package com.github.elenterius.biomancy.world.block.modularlarynx;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.world.block.property.BlockPropertyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

import javax.annotation.Nullable;

@Deprecated
public class VoiceBoxBlock extends BaseEntityBlock {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

	public VoiceBoxBlock(Properties properties) {
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(NOTE, 0).setValue(POWERED, Boolean.FALSE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED, NOTE);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.VOICE_BOX.get().create(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		ItemStack heldStack = player.getItemInHand(hand);
		if (!heldStack.isEmpty()) {
			if (VoiceBoxBlockEntity.VALID_ITEM.test(heldStack) && level.getBlockEntity(pos) instanceof VoiceBoxBlockEntity voiceBox) {
				ItemStack storedStack = voiceBox.getStoredItemStack();
				if (storedStack.isEmpty()) {
					voiceBox.setStoredItemStack(heldStack.copy());
					heldStack.shrink(1);
					return InteractionResult.CONSUME;
				}
			}
		}
		else if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof VoiceBoxBlockEntity voiceBox) {
			ItemStack storedStack = voiceBox.getStoredItemStack();
			if (!storedStack.isEmpty()) {
				voiceBox.dropAllInvContents(level, pos);
				voiceBox.setStoredItemStack(ItemStack.EMPTY);
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
		if (!level.isClientSide && level.isEmptyBlock(pos.above()) && level.getBlockEntity(pos) instanceof VoiceBoxBlockEntity voiceBox) {
			int note = state.getValue(NOTE);
			float pitch = (float) Math.pow(2d, (note - 12) / 12d);
			if (!voiceBox.playVoice(3f, pitch)) {
				level.playSound(null, pos, SoundEvents.PLAYER_BREATH, SoundSource.RECORDS, 3f, pitch);
			}
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
			if (tileEntity instanceof VoiceBoxBlockEntity voiceBox) {
				voiceBox.dropAllInvContents(level, pos);
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

}
