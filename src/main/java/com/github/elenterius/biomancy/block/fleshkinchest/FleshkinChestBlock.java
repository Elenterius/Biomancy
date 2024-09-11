package com.github.elenterius.biomancy.block.fleshkinchest;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.ownable.OwnableEntityBlock;
import com.github.elenterius.biomancy.permission.Actions;
import com.github.elenterius.biomancy.permission.IRestrictedInteraction;
import com.github.elenterius.biomancy.permission.UserType;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class FleshkinChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, OwnableEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final VoxelShape SHAPE_NORTH_OR_SOUTH = Block.box(0, 0, 1, 16, 13, 15);
	public static final VoxelShape SHAPE_WEST_OR_EAST = Block.box(1, 0, 0, 15, 13, 16);
	private final float destroySpeed;

	public FleshkinChestBlock(Properties properties, float destroySpeed) {
		super(properties.destroyTime(-1).explosionResistance(1200)); //set destroy speed to -1f to make the block unbreakable for the Create Drill & other stuff
		this.destroySpeed = destroySpeed;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	public ItemStack createItemStackForCreativeTab() {
		ItemStack stack = new ItemStack(this);
		stack.setHoverName(ComponentUtil.literal("[TEST/other_owner] ").append(stack.getHoverName()));
		CompoundTag tag = new CompoundTag();
		tag.putUUID(OwnableEntityBlock.NBT_KEY_OWNER, Util.NIL_UUID);
		BlockItem.setBlockEntityData(stack, ModBlockEntities.FLESHKIN_CHEST.get(), tag);
		return stack;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof FleshkinChestBlockEntity chest) {
			if (stack.hasCustomHoverName()) {
				chest.setCustomName(stack.getHoverName());
			}

			OwnableEntityBlock.setBlockEntityOwner(level, chest, placer, stack);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.FLESHKIN_CHEST.get().create(pos, state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		if (Boolean.TRUE.equals(state.getValue(WATERLOGGED))) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}


	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		if (player instanceof ServerPlayer serverPlayer && !ChestBlock.isChestBlockedAt(level, pos)) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof FleshkinChestBlockEntity chest) {
				if (chest.canPlayerOpenContainer(player)) {

					ItemStack stack = player.getItemInHand(hand);
					if (stack.getItem() instanceof EssenceItem essenceItem) {
						if (chest.isActionAllowed(player, Actions.CONFIGURE)) {
							boolean success = essenceItem.getEntityUUID(stack).map(chest::addUser).orElse(false);
							if (success) {
								stack.shrink(1);
								level.playSound(null, pos, ModSoundEvents.FLESHKIN_EAT.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
								return InteractionResult.SUCCESS;
							}
						}

						level.playSound(null, pos, ModSoundEvents.FLESHKIN_NO.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
						return InteractionResult.CONSUME;
					}

					MenuProvider menuProvider = getMenuProvider(state, level, pos);
					if (menuProvider != null) {
						NetworkHooks.openScreen(serverPlayer, menuProvider, byteBuffer -> {});
						return InteractionResult.SUCCESS;
					}
				}
				else {
					if (level.random.nextFloat() < 0.6f) {
						chest.attack(state.getValue(FACING), player);
						level.playSound(null, pos, ModSoundEvents.FLESHKIN_CHEST_BITE_ATTACK.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
					}
					else {
						int particleCount = level.random.nextInt(1, 3);
						((ServerLevel) level).sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, particleCount, 0.5d, 0.25d, 0.5d, 0);
						level.playSound(null, pos, ModSoundEvents.FLESHKIN_NO.get(), SoundSource.BLOCKS, 1f, level.random.nextFloat() * 0.1f + 0.9f);
					}
				}
			}
		}

		return InteractionResult.CONSUME; //prevent accidental placement of blocks, etc
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!level.isClientSide && !state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof FleshkinChestBlockEntity chest) {
			chest.dropContainerContents(level, pos);
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		return false;
	}

	@Override
	public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
		//ignore the explosion, the block is not destroyed
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof IRestrictedInteraction interaction && interaction.isActionAllowed(player, Actions.DESTROY_BLOCK)) {
			int i = ForgeHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
			return player.getDigSpeed(state, pos) / destroySpeed / i;
		}
		return 0;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
		if (level.getBlockEntity(pos) instanceof FleshkinChestBlockEntity chest) {
			chest.saveToItem(stack);
		}
		return stack;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof FleshkinChestBlockEntity chest) {
			return AbstractContainerMenu.getRedstoneSignalFromContainer(chest.getInventory());
		}
		return 0;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case NORTH, SOUTH -> SHAPE_NORTH_OR_SOUTH;
			case WEST, EAST -> SHAPE_WEST_OR_EAST;
			default -> Shapes.block();
		};
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);

		OwnableEntityBlock.appendUserListToTooltip(stack, tooltip);

		if (Minecraft.getInstance().player == null) return;

		CompoundTag tag = BlockItem.getBlockEntityData(stack);
		if (tag != null) {
			tooltip.add(ComponentUtil.emptyLine());

			if (isAuthorized(Minecraft.getInstance().player.getUUID(), tag)) {
				CompoundTag inventoryTag = tag.getCompound("Inventory");
				if (!inventoryTag.isEmpty() && inventoryTag.contains("Items", Tag.TAG_LIST)) {
					int size = inventoryTag.contains("Size") ? inventoryTag.getInt("Size") : FleshkinChestBlockEntity.SLOTS;
					NonNullList<ItemStack> itemList = NonNullList.withSize(size, ItemStack.EMPTY);
					ContainerHelper.loadAllItems(inventoryTag, itemList);
					int count = 0;
					int totalCount = 0;

					for (ItemStack storedStack : itemList) {
						if (!storedStack.isEmpty()) {
							totalCount++;
							if (count < 5) {
								count++;
								MutableComponent textComponent = storedStack.getHoverName().copy();
								textComponent.append(" x").append(String.valueOf(storedStack.getCount())).withStyle(ChatFormatting.GRAY);
								tooltip.add(textComponent);
							}
						}
					}

					if (totalCount - count > 0) {
						tooltip.add((ComponentUtil.translatable("container.shulkerBox.more", totalCount - count)).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
					}
					tooltip.add(ComponentUtil.emptyLine());
					tooltip.add(ComponentUtil.literal(String.format("%d/%d ", totalCount, FleshkinChestBlockEntity.SLOTS)).append(ComponentUtil.translatable("tooltip.biomancy.slots")).withStyle(ChatFormatting.GRAY));
				}
			}
			else {
				tooltip.add(ComponentUtil.literal("Who are you? I don't like you!").withStyle(TextStyles.PRIMORDIAL_RUNES_GRAY));
			}
		}
	}

	private boolean isAuthorized(UUID uuid, CompoundTag tag) {
		if (tag.hasUUID(NBT_KEY_OWNER)) {
			if (tag.getUUID(NBT_KEY_OWNER).equals(uuid)) return true;
		}

		if (tag.contains(NBT_KEY_USER_LIST)) {
			ListTag nbtList = tag.getList(NBT_KEY_USER_LIST, Tag.TAG_COMPOUND);
			for (int i = 0; i < nbtList.size(); i++) {
				CompoundTag userTag = nbtList.getCompound(i);
				UUID userUUID = userTag.getUUID("UserUUID");
				UserType authority = UserType.deserialize(userTag);
				if (userUUID.equals(uuid) && authority.isUserLevel()) return true;
			}
		}

		return false;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof FleshkinChestBlockEntity chest) {
			chest.recheckOpen();
		}
	}

}
