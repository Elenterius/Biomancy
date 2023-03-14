package com.github.elenterius.biomancy.world.block.cradle;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.ModTriggers;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PrimordialCradleBlock extends HorizontalDirectionalBlock implements EntityBlock {

	public static final Predicate<ItemStack> EXPENSIVE_ITEMS = stack -> {
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem && (blockItem.getBlock() instanceof ShulkerBoxBlock || blockItem.getBlock() instanceof FleshkinChestBlock))
			return true;
		return item instanceof TieredItem || item instanceof Vanishable || stack.isEnchanted();
	};
	protected static final VoxelShape INSIDE_AABB = box(3, 4, 3, 13, 16, 13);
	protected static final VoxelShape OUTSIDE_AABB = Stream.of(
			box(1, 0, 1, 15, 5, 15),
			box(0, 5, 0, 16, 12, 16),
			box(1, 12, 1, 15, 16, 15)
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	protected static final VoxelShape AABB = Shapes.join(OUTSIDE_AABB, INSIDE_AABB, BooleanOp.ONLY_FIRST);

	public PrimordialCradleBlock(Properties properties) {
		super(properties);
	}

	public static float getYRotation(BlockState state) {
		return state.getValue(FACING).toYRot();
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> blockEntityType, BlockEntityType<E> targetType, BlockEntityTicker<? super E> entityTicker) {
		//noinspection unchecked
		return targetType == blockEntityType ? (BlockEntityTicker<A>) entityTicker : null;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.PRIMORDIAL_CRADLE.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.PRIMORDIAL_CRADLE.get(), PrimordialCradleBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stackInHand = player.getItemInHand(hand);
		if (increaseFillLevel(player, level, pos, stackInHand)) {
			if (!level.isClientSide) {
				boolean isPotion = stackInHand.getItem() instanceof PotionItem;

				if (!player.isCreative()) {
					stackInHand.shrink(1);
				}

				if (stackInHand.hasCraftingRemainingItem()) {
					player.getInventory().add(stackInHand.getCraftingRemainingItem());
				}
				else if (isPotion && stackInHand.isEmpty()) {
					player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
				}

			}
			return InteractionResult.SUCCESS;
		}
		if (!level.isClientSide) {
			SoundUtil.broadcastBlockSound((ServerLevel) level, pos, ModSoundEvents.CREATOR_NO);
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide() && entity instanceof ItemEntity itemEntity) {
			ItemStack stack = itemEntity.getItem();

			if (stack.is(ModItems.LIVING_FLESH.get())) {
				UUID thrower = itemEntity.getThrower();
				if (thrower == null && itemEntity.getAge() < 80) {
					return;
				}
			}

			if (increaseFillLevel(null, level, pos, stack)) {
				if (stack.hasCraftingRemainingItem()) {
					entity.spawnAtLocation(stack.getCraftingRemainingItem());
				}
				else if (stack.getItem() instanceof PotionItem) {
					entity.spawnAtLocation(new ItemStack(Items.GLASS_BOTTLE));
				}
				stack.shrink(1);
			}
		}
	}

	private boolean increaseFillLevel(@Nullable Player player, Level level, BlockPos pos, ItemStack stack) {
		if (!stack.isEmpty() && !level.isClientSide()) {
			if (EXPENSIVE_ITEMS.test(stack)) return false;

			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof PrimordialCradleBlockEntity creator && !creator.isFull() && creator.insertItem(ItemHandlerHelper.copyStackWithSize(stack, 1))) {
				if (player != null) {
					ModTriggers.SACRIFICED_ITEM_TRIGGER.trigger((ServerPlayer) player, stack);
				}
				SoundEvent soundEvent = creator.isFull() ? ModSoundEvents.CREATOR_BECAME_FULL.get() : ModSoundEvents.CREATOR_EAT.get();
				SoundUtil.broadcastBlockSound((ServerLevel) level, pos, soundEvent);
				return true;
			}
		}
		return false;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(4) == 0 && level.getBlockEntity(pos) instanceof PrimordialCradleBlockEntity creator && creator.isFull()) {
			int particleAmount = random.nextInt(2, 8);
			int color = 0x9f4576; //magenta haze
			double r = (color >> 16 & 255) / 255d;
			double g = (color >> 8 & 255) / 255d;
			double b = (color & 255) / 255d;
			for (int i = 0; i < particleAmount; i++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + ((random.nextFloat() * 0.60625) + 0.13125f), pos.getY() + 0.5f, pos.getZ() + ((random.nextFloat() * 0.60625) + 0.13125f), r, g, b);
			}
			if (random.nextInt(3) == 0) {
				SoundUtil.clientPlayBlockSound(level, pos, ModSoundEvents.CREATOR_CRAFTING_RANDOM, 0.85f);
			}
		}
	}

}
