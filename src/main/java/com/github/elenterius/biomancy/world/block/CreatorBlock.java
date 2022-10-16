package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CreatorBlock extends HorizontalDirectionalBlock implements EntityBlock {

	protected static final VoxelShape INSIDE_AABB = box(3, 4, 3, 13, 16, 13);
	protected static final VoxelShape OUTSIDE_AABB = Stream.of(
			box(1, 0, 1, 15, 5, 15),
			box(0, 5, 0, 16, 12, 16),
			box(1, 12, 1, 15, 16, 15)
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	protected static final VoxelShape AABB = Shapes.join(OUTSIDE_AABB, INSIDE_AABB, BooleanOp.ONLY_FIRST);

	public static final Predicate<ItemStack> EXPENSIVE_ITEMS = stack -> {
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem && (blockItem.getBlock() instanceof ShulkerBoxBlock || blockItem.getBlock() instanceof FleshkinChestBlock))
			return true;
		return stack.getItemEnchantability() > 0 || stack.hasFoil() || stack.isEnchanted() || item instanceof TieredItem || item instanceof Vanishable;
	};

	public CreatorBlock(Properties properties) {
		super(properties);
	}

	public static float getYRotation(BlockState state) {
		return state.getValue(FACING).toYRot();
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
		return ModBlockEntities.CREATOR.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.CREATOR.get(), CreatorBlockEntity::serverTick);
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> blockEntityType, BlockEntityType<E> targetType, BlockEntityTicker<? super E> entityTicker) {
		//noinspection unchecked
		return targetType == blockEntityType ? (BlockEntityTicker<A>) entityTicker : null;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stackInHand = player.getItemInHand(hand);
		if (increaseFillLevel(player, level, pos, stackInHand)) {
			return InteractionResult.SUCCESS;
		}
		level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 0.75f, level.random.nextFloat(0.15f, 0.45f));
		return InteractionResult.CONSUME;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide() && entity instanceof ItemEntity itemEntity) {
			increaseFillLevel(null, level, pos, itemEntity.getItem());
		}
	}

	private boolean increaseFillLevel(@Nullable Player player, Level level, BlockPos pos, ItemStack stack) {
		if (!stack.isEmpty() && !level.isClientSide()) {
			if (EXPENSIVE_ITEMS.test(stack)) return false;

			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof CreatorBlockEntity creatorEntity && !creatorEntity.isFull() && creatorEntity.insertItem(ItemHandlerHelper.copyStackWithSize(stack, 1))) {
				if (player == null || !player.isCreative()) stack.shrink(1);
				level.playSound(null, pos, creatorEntity.isFull() ? SoundEvents.PLAYER_BURP : SoundEvents.GOAT_SCREAMING_EAT, SoundSource.BLOCKS, 1f, level.random.nextFloat(0.25f, 0.5f));
				return true;
			}
		}
		return false;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (random.nextInt(4) == 0 && level.getBlockEntity(pos) instanceof CreatorBlockEntity creator && creator.getFillLevel() >= creator.getMaxFillLevel()) {
			for (int i = 0; i < random.nextInt(2, 8); i++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.13125f + 0.7375f * random.nextFloat(), pos.getY() + 0.5f, pos.getZ() + 0.13125f + 0.7375f * random.nextFloat(), 1.8f, 1.4f, 1.4f);
			}
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AABB;
	}

}
