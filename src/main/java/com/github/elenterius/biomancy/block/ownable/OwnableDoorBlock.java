package com.github.elenterius.biomancy.block.ownable;

import com.github.elenterius.biomancy.block.base.IBlockEntityDelegator;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.ownable.Ownable;
import com.github.elenterius.biomancy.ownable.OwnableEntityBlock;
import com.github.elenterius.biomancy.permission.Actions;
import com.github.elenterius.biomancy.permission.IRestrictedInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OwnableDoorBlock extends DoorBlock implements OwnableEntityBlock {

	public static final int UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE; //10

	public OwnableDoorBlock(Properties properties, BlockSetType type) {
		super(properties, type);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? ModBlockEntities.OWNABLE_BE.get().create(pos, state) : ModBlockEntities.BE_DELEGATOR.get().create(pos, state);
	}

	public boolean isInteractionAllowed(BlockState state, Level level, BlockPos pos, @Nullable Entity entity) {
		BlockEntity blockEntity = getCorrectBlockEntity(state, level, pos);
		if (blockEntity instanceof IRestrictedInteraction restricted) {
			if (entity == null) return false;
			return restricted.isActionAllowed(entity, Actions.USE_BLOCK);
		}
		return true;
	}

	@Nullable
	public BlockEntity getCorrectBlockEntity(BlockState state, BlockGetter level, final BlockPos posIn) {
		BlockPos mainPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? posIn.below() : posIn;
		BlockEntity blockEntity = level.getBlockEntity(mainPos);

		if (blockEntity instanceof IBlockEntityDelegator delegator) {
			return delegator.getDelegate();
		}

		return blockEntity;
	}

	public boolean isPowered(BlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, final BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

		if (!(getCorrectBlockEntity(state, level, pos) instanceof IRestrictedInteraction restrictedBlock)) return InteractionResult.PASS;
		if (!restrictedBlock.isActionAllowed(player, Actions.USE_BLOCK)) return InteractionResult.PASS;

		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem() instanceof EssenceItem essenceItem) {
			if (level.isClientSide) return InteractionResult.SUCCESS;

			if (restrictedBlock.isActionAllowed(player, Actions.CONFIGURE)) {
				boolean success = essenceItem.getEntityUUID(stack).map(restrictedBlock::addUser).orElse(false);
				if (success) {
					stack.shrink(1);
					level.playSound(null, pos, ModSoundEvents.FLESHKIN_EAT.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
					return InteractionResult.SUCCESS;
				}
			}

			level.playSound(null, pos, ModSoundEvents.FLESHKIN_NO.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
			return InteractionResult.CONSUME;
		}

		state = state.cycle(OPEN);
		level.setBlock(pos, state, UPDATE_FLAGS);
		boolean isOpen = isOpen(state);
		playSound(player, level, pos, isOpen);

		//handle connected door (open/close double door feature)
		boolean isRightHingeSide = state.getValue(HINGE) == DoorHingeSide.RIGHT;
		Direction direction = state.getValue(FACING);
		BlockPos connectedPos = switch (direction) {
			case SOUTH -> pos.relative(isRightHingeSide ? Direction.EAST : Direction.WEST);
			case WEST -> pos.relative(isRightHingeSide ? Direction.SOUTH : Direction.NORTH);
			case NORTH -> pos.relative(isRightHingeSide ? Direction.WEST : Direction.EAST);
			default -> pos.relative(isRightHingeSide ? Direction.NORTH : Direction.SOUTH);
		};

		BlockState connectedState = level.getBlockState(connectedPos);
		if (connectedState.is(this) && connectedState.getValue(FACING) == direction && connectedState.getValue(HINGE) != state.getValue(HINGE)) { //check if it is a door with an opposite hinge
			if (isInteractionAllowed(connectedState, level, connectedPos, player)) {
				if (isOpen(connectedState) != isOpen) { //only updated connected door if its open state mismatches the targetState
					connectedState = connectedState.setValue(OPEN, isOpen);
					level.setBlock(connectedPos, connectedState, UPDATE_FLAGS);
					playSound(player, level, connectedPos, isOpen);
				}
			}
		}

		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public void setOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open) {
		if (state.is(this) && open != isOpen(state)) {
			if (!isInteractionAllowed(state, level, pos, entity)) return;

			level.setBlock(pos, state.setValue(OPEN, open), UPDATE_FLAGS);
			playSound(null, level, pos, open);
			level.gameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, final BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
		if (level.isClientSide()) return;
		if (neighborBlock == this) return;

		DoubleBlockHalf half = state.getValue(HALF);
		if (getCorrectBlockEntity(state, level, pos) instanceof IRestrictedInteraction restricted) {
			boolean isAllowed = false;

			//check if th owner of the neighbor is allowed to interact with this block
			if (neighborBlock instanceof OwnableEntityBlock && level.getBlockState(neighborPos).is(neighborBlock)) { //only allow "direct" neighbors
				if (level.getBlockEntity(neighborPos) instanceof Ownable neighbor) {
					Optional<UUID> neighborOwner = neighbor.getOptionalOwnerUUID();
					if (neighborOwner.isPresent()) {
						isAllowed = restricted.isActionAllowed(neighborOwner.get(), Actions.USE_BLOCK);
					}
				}
			}

			boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(half == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));

			if (isAllowed) {
				handleAllowedSignal(state, level, pos, hasSignal);
				return;
			}

			handleForbiddenSignal(state, level, pos, hasSignal, isPowered(state));
		}
	}

	private void handleForbiddenSignal(BlockState state, Level level, BlockPos posIn, boolean hasSignal, boolean isPowered) {
		if (hasSignal == isPowered) return;

		if (isOpen(state)) { //force close the door if open
			playSound(null, level, posIn, false);
			state = state.setValue(OPEN, false);
			level.gameEvent(null, GameEvent.BLOCK_CLOSE, posIn);
		}
		else if (hasSignal) {
			level.playSound(null, posIn, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1f, 1f);
		}
		level.setBlock(posIn, state.setValue(POWERED, hasSignal), Block.UPDATE_CLIENTS);
	}

	private void handleAllowedSignal(BlockState state, Level level, BlockPos pos, boolean hasSignal) {
		if (hasSignal == isOpen(state)) return; //door is already open or closed

		playSound(null, level, pos, hasSignal);
		level.setBlock(pos, state.setValue(POWERED, hasSignal).setValue(OPEN, hasSignal), Block.UPDATE_CLIENTS);
		level.gameEvent(null, hasSignal ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf half = state.getValue(HALF);
		if ((facing.getAxis() == Direction.Axis.Y) && (half == DoubleBlockHalf.LOWER == (facing == Direction.UP))) { //check if current state is the lower half and facing state is the upper half
			if (facingState.is(this) && facingState.getValue(HALF) != half) { //check if upper half is a different door half
				return state.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, isPowered(facingState));
			}
			return Blocks.AIR.defaultBlockState(); //top door half is missing, set the lower half to air as well
		}
		else {
			//don't check if door is standing on a block, let it float
			return state;
		}
	}

	protected void playSound(@Nullable Player player, Level level, BlockPos pos, boolean isOpening) {
		level.playSound(player, pos, isOpening ? type().trapdoorOpen() : type().trapdoorClose(), SoundSource.BLOCKS, 1f, level.getRandom().nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		OwnableEntityBlock.appendUserListToTooltip(stack, tooltip);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof Ownable ownable) {
			OwnableEntityBlock.setBlockEntityOwner(level, ownable, placer, stack);
		}

		if (blockEntity instanceof IBlockEntityDelegator delegator && level.getBlockEntity(pos.below()) instanceof OwnableBlockEntity ownable) {
			delegator.setDelegate(ownable);
		}
	}

	@Override
	public void playerWillDestroy(Level level, final BlockPos posIn, BlockState state, Player player) {
//		BlockPos pos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? posIn.below() : posIn;
//		dropForCreativePlayer(level, this, pos, player);
		super.playerWillDestroy(level, posIn, state, player);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		if (getCorrectBlockEntity(state, level, pos) instanceof IRestrictedInteraction interaction && interaction.isActionAllowed(player, Actions.DESTROY_BLOCK)) {
			return super.getDestroyProgress(state, player, level, pos);
		}
		return 0f;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

}
