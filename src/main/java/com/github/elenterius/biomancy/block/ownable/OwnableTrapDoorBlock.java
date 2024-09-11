package com.github.elenterius.biomancy.block.ownable;

import com.github.elenterius.biomancy.block.property.UserSensitivity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.ownable.Ownable;
import com.github.elenterius.biomancy.ownable.OwnableEntityBlock;
import com.github.elenterius.biomancy.permission.Actions;
import com.github.elenterius.biomancy.permission.IRestrictedInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OwnableTrapDoorBlock extends TrapDoorBlock implements OwnableEntityBlock {

	public static final EnumProperty<UserSensitivity> SENSITIVITY = ModBlockProperties.USER_SENSITIVITY;
	protected static final AABB TOP_AABB_VOLUME = new AABB(0d, 0.75d, 0d, 1d, 1.25d, 1d);
	protected static final AABB BOTTOM_AABB_VOLUME = new AABB(0d, -0.25d, 0d, 1d, 0.25d, 1d);
	protected static final AABB INFLATED_AABB_VOLUME = new AABB(-0.125d, -0.25d, -0.125d, 1.125d, 1.25d, 1.125d);
	protected static final VoxelShape BOTTOM_COLLISION_SHAPE = Block.box(0d, 0.1d, 0d, 16d, 3d, 16d);

	public OwnableTrapDoorBlock(Properties properties, BlockSetType type) {
		super(properties, type);
		registerDefaultState(defaultBlockState().setValue(SENSITIVITY, UserSensitivity.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SENSITIVITY);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.OWNABLE_BE.get().create(pos, state);
	}

	public boolean isPowered(BlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (!isOpen(state) && state.getValue(HALF) == Half.BOTTOM) {
			return hasCollision ? BOTTOM_COLLISION_SHAPE : Shapes.empty(); //substitute collision shape to enable entity collision from below
			//Note: onEntityCollision is only called when the entity intersects a "block pos" (collided block positions ~= floor(entityAABB.minPos), ..., floor(entityAABB.maxPos))
		}
		return super.getCollisionShape(state, level, pos, context);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction restrictedBlock && restrictedBlock.isActionAllowed(player, Actions.USE_BLOCK)) {

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

			if (player.isShiftKeyDown()) {
				state = state.cycle(SENSITIVITY);
				level.setBlock(pos, state, Block.UPDATE_CLIENTS);
				if (isWaterlogged(state)) {
					level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			state = state.cycle(OPEN);
			level.setBlock(pos, state, Block.UPDATE_CLIENTS);
			if (isWaterlogged(state)) {
				level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
			}
			playSound(player, level, pos, isOpen(state));
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		if (isWaterlogged(state)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void playSound(@Nullable Player player, Level level, BlockPos pos, boolean isOpened) {
		level.playSound(player, pos, isOpened ? type.trapdoorOpen() : type.trapdoorClose(), SoundSource.BLOCKS, 1f, level.getRandom().nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
		if (level.isClientSide()) return;

		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction restricted) {
			boolean isAllowed = false;

			// check if th owner of the neighbor is allowed to interact with this block
			if (neighborBlock instanceof OwnableEntityBlock && level.getBlockState(neighborPos).is(neighborBlock)) { //only allow "direct" neighbors
				if (level.getBlockEntity(neighborPos) instanceof Ownable neighbor) {
					Optional<UUID> neighborOwner = neighbor.getOptionalOwnerUUID();
					if (neighborOwner.isPresent()) {
						isAllowed = restricted.isActionAllowed(neighborOwner.get(), Actions.USE_BLOCK);
					}
				}
			}

			boolean hasSignal = level.hasNeighborSignal(pos);
			boolean isPowered = isPowered(state);

			if (isAllowed) {
				handleAllowedSignal(state, level, pos, hasSignal, isPowered);
				return;
			}

			handleForbiddenSignal(state, level, pos, hasSignal, isPowered);
		}
	}

	private void handleForbiddenSignal(BlockState state, Level level, BlockPos pos, boolean hasSignal, boolean isPowered) {
		if (hasSignal == isPowered) return;

		if (isOpen(state)) {
			playSound(null, level, pos, false);
			state = state.setValue(OPEN, false);
			level.gameEvent(null, GameEvent.BLOCK_CLOSE, pos);
		}
		else if (hasSignal) {
			level.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1f, 1f);
		}
		level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_CLIENTS);

		if (isWaterlogged(state)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
	}

	private void handleAllowedSignal(BlockState state, Level level, BlockPos pos, boolean hasSignal, boolean isPowered) {
		if (hasSignal == isPowered) return;

		if (hasSignal != isOpen(state)) {
			playSound(null, level, pos, hasSignal);
			state = state.setValue(OPEN, hasSignal);
			level.gameEvent(null, hasSignal ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		}
		level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_CLIENTS);

		if (isWaterlogged(state)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		if (!isOpen(state)) return;

		UserSensitivity sensitivity = state.getValue(SENSITIVITY);
		if (!sensitivity.isNone() && level.getBlockEntity(pos) instanceof IRestrictedInteraction restricted) {
			AABB aabb = INFLATED_AABB_VOLUME.move(pos);
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb);

			for (LivingEntity livingEntity : list) {
				if (livingEntity.isSteppingCarefully()) continue;

				boolean actionAllowed = restricted.isActionAllowed(livingEntity, Actions.USE_BLOCK);
				if (sensitivity == UserSensitivity.HOSTILE) {
					if (!actionAllowed) {
						// if block is inverted keep door open when not authorized (trap mode)
						level.scheduleTick(pos, state.getBlock(), 60); //schedule tick for the next close attempt (~3sec)
						return;
					}
				}
				else if (actionAllowed) {
					// when normal only keep door open for authorized users
					level.scheduleTick(pos, state.getBlock(), 60); //schedule tick for the next close attempt (~3sec)
					return;
				}
			}
		}

		level.setBlock(pos, state.setValue(OPEN, false), Block.UPDATE_CLIENTS);
		level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1f, 1f);
		level.gameEvent(null, GameEvent.BLOCK_CLOSE, pos);
	}

	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return false; //return early
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		openDoorOnEntityCollision(state, level, pos, entity);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		openDoorOnEntityCollision(state, level, pos, entity);
	}

	protected void openDoorOnEntityCollision(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide()) return;
		if (isOpen(state) || !(entity instanceof LivingEntity)) return;

		UserSensitivity sensitivity = state.getValue(SENSITIVITY);
		if (!sensitivity.isNone() && level.getBlockEntity(pos) instanceof IRestrictedInteraction restricted) {
			AABB aabb = state.getValue(HALF) == Half.TOP ? TOP_AABB_VOLUME.move(pos) : BOTTOM_AABB_VOLUME.move(pos);
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb);

			for (LivingEntity livingEntity : list) {
				if (livingEntity.isSteppingCarefully()) continue;

				boolean actionAllowed = restricted.isActionAllowed(livingEntity, Actions.USE_BLOCK);
				if (sensitivity == UserSensitivity.HOSTILE) {
					if (!actionAllowed) {
						openTrapDoor(level, state, pos, true);
						return;
					}
				}
				else if (actionAllowed) {
					openTrapDoor(level, state, pos, true);
					return;
				}
			}
		}
	}

	public void openTrapDoor(Level level, BlockState state, BlockPos pos, boolean autoClose) {
		if (level.isClientSide()) return;
		if (isOpen(state)) return;

		state = state.setValue(OPEN, true);
		level.setBlock(pos, state, Block.UPDATE_CLIENTS);
		level.gameEvent(null, GameEvent.BLOCK_OPEN, pos);

		if (autoClose) level.scheduleTick(pos, state.getBlock(), 40); //schedule tick to auto-close door after (~2sec)
		if (isWaterlogged(state)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1f, 1f);
	}

	public boolean isWaterlogged(BlockState state) {
		return state.getValue(WATERLOGGED);
	}

	public boolean isOpen(BlockState state) {
		return state.getValue(OPEN);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		OwnableEntityBlock.appendUserListToTooltip(stack, tooltip);
	}

//	@Override
//	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
//		dropForCreativePlayer(level, this, pos, player);
//		super.playerWillDestroy(level, pos, state, player);
//	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (level.getBlockEntity(pos) instanceof Ownable ownable) {
			OwnableEntityBlock.setBlockEntityOwner(level, ownable, placer, stack);
		}
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction interaction && interaction.isActionAllowed(player, Actions.DESTROY_BLOCK)) {
			return super.getDestroyProgress(state, player, level, pos);
		}
		return 0f;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

}
