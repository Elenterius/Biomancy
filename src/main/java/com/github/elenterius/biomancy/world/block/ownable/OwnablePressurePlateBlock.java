package com.github.elenterius.biomancy.world.block.ownable;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.world.block.property.UserSensitivity;
import com.github.elenterius.biomancy.world.ownable.IOwnable;
import com.github.elenterius.biomancy.world.ownable.IOwnableEntityBlock;
import com.github.elenterius.biomancy.world.permission.Actions;
import com.github.elenterius.biomancy.world.permission.IRestrictedInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class OwnablePressurePlateBlock extends PressurePlateBlock implements IOwnableEntityBlock {

	public static final EnumProperty<UserSensitivity> USER_SENSITIVITY = ModBlocks.USER_SENSITIVITY_PROPERTY;
	public static final Predicate<Entity> ENTITY_SELECTOR = entity -> !entity.isSpectator() && !entity.isIgnoringBlockTriggers();

	public OwnablePressurePlateBlock(Properties properties) {
		super(Sensitivity.MOBS, properties);
		registerDefaultState(defaultBlockState().setValue(USER_SENSITIVITY, UserSensitivity.FRIENDLY));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(USER_SENSITIVITY);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.OWNABLE_BE.get().create(pos, state);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (level.getBlockEntity(pos) instanceof IOwnable ownable) {
			IOwnableEntityBlock.setupBlockEntityOwner(level, ownable, placer, stack);
		}
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return state; //don't check if pressure plate is standing on a block, let it float
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction interaction && interaction.isActionAllowed(player, Actions.USE_BLOCK)) {
			if (player.isShiftKeyDown()) {
				state = state.setValue(USER_SENSITIVITY, state.getValue(USER_SENSITIVITY).cycle());
				level.setBlock(pos, state, Block.UPDATE_CLIENTS);
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	protected int getSignalStrength(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction interaction) {

			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, TOUCH_AABB.move(pos), ENTITY_SELECTOR);
			if (!list.isEmpty()) {
				BlockState state = level.getBlockState(pos);
				UserSensitivity sensitivity = state.getValue(USER_SENSITIVITY);

				if (sensitivity == UserSensitivity.HOSTILE) {
					for (Entity entity : list) {
						if (!interaction.isActionAllowed(entity.getUUID(), Actions.USE_BLOCK)) {
							return 15;
						}
					}
					return 0;
				}

				if (sensitivity == UserSensitivity.FRIENDLY) {
					for (Entity entity : list) {
						if (interaction.isActionAllowed(entity.getUUID(), Actions.USE_BLOCK)) {
							return 15;
						}
					}
				}
			}
		}
		return 0;
	}

//	@Override
//	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
//		dropForCreativePlayer(worldIn, this, pos, player);
//		super.playerWillDestroy(worldIn, pos, state, player);
//	}

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

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter pLevel, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, pLevel, tooltip, flag);
		IOwnableEntityBlock.appendUserListToTooltip(stack, tooltip);
	}

}
