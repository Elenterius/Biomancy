package com.github.elenterius.biomancy.block.cradle;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.biomancy.world.spatial.SpatialShapeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
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

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PrimordialCradleBlock extends HorizontalDirectionalBlock implements EntityBlock {

	public static final Predicate<ItemStack> CANNOT_BE_SACRIFICED = stack -> {
		if (stack.is(ModItemTags.CANNOT_BE_EATEN_BY_CRADLE)) return true;

		Item item = stack.getItem();

		if (item instanceof TieredItem || item instanceof Vanishable) return true;

		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();

			//prevent all items that have a BlockEntity associated with it from being sacrificed
			// e.g. complex modded blocks such as computers, machines, containers, etc.
			if (block instanceof EntityBlock) return true;
		}

		if (ModsCompatHandler.getTetraHelper().isToolOrModularItem(item)) return true;

		if (stack.isEnchanted()) return true;
		if (!item.canFitInsideContainerItems()) return true;
		return stack.getCapability(ModCapabilities.ITEM_HANDLER).isPresent();
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

	public static int getPrimalEnergy(CompoundTag tag) {
		return tag.contains(PrimordialCradleBlockEntity.PRIMAL_ENERGY_KEY) ? tag.getInt(PrimordialCradleBlockEntity.PRIMAL_ENERGY_KEY) : 0;
	}

	public static MutableComponent createValueComponent(DecimalFormat df, int value, String name) {
		return ComponentUtil.literal(df.format(value))
				.withStyle(TextStyles.PRIMORDIAL_RUNES_LIGHT_GRAY)
				.append(ComponentUtil.space())
				.append(ComponentUtil.literal(name).withStyle(TextStyles.GRAY));
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

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level instanceof ServerLevel serverLevel) {
				SpatialShapeManager.remove(serverLevel, pos); //removes mound shape from level
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.PRIMORDIAL_CRADLE.get(), PrimordialCradleBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);

		if (increaseFillLevel(player, level, pos, ItemHandlerHelper.copyStackWithSize(stack, 1))) {
			if (!level.isClientSide) {
				boolean isPotion = stack.getItem() instanceof PotionItem;

				if (!player.isCreative()) {
					stack.shrink(1);
				}

				if (stack.hasCraftingRemainingItem()) {
					player.getInventory().add(stack.getCraftingRemainingItem());
				}
				else if (isPotion && stack.isEmpty()) {
					player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
				}

			}
			return InteractionResult.SUCCESS;
		}
		if (!level.isClientSide) {
			SoundUtil.broadcastBlockSound((ServerLevel) level, pos, ModSoundEvents.CRADLE_NO);
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide() && entity instanceof ItemEntity itemEntity) {
			ItemStack stack = itemEntity.getItem();
			Entity thrower = itemEntity.getOwner();

			if (stack.is(ModItems.LIVING_FLESH.get()) && thrower == null && itemEntity.getAge() < 80) {
				return;
			}

			if (increaseFillLevel(thrower, level, pos, stack)) {
				if (stack.hasCraftingRemainingItem()) {
					entity.spawnAtLocation(stack.getCraftingRemainingItem());
				}
				else if (stack.getItem() instanceof PotionItem) {
					entity.spawnAtLocation(new ItemStack(Items.GLASS_BOTTLE));
				}
			}
		}
	}

	private boolean increaseFillLevel(@Nullable Entity player, Level level, BlockPos pos, ItemStack stack) {
		if (level.isClientSide()) return false;
		if (stack.isEmpty()) return false;
		if (CANNOT_BE_SACRIFICED.test(stack)) return false;

		ItemStack copyOfStack = ItemHandlerHelper.copyStackWithSize(stack, 1); //cradle#insertItem modifies the stack which may lead to it being empty
		if (level.getBlockEntity(pos) instanceof PrimordialCradleBlockEntity cradle && !cradle.isFull() && cradle.insertItem(stack)) {
			if (player instanceof ServerPlayer serverPlayer) {
				ModTriggers.SACRIFICED_ITEM_TRIGGER.trigger(serverPlayer, copyOfStack);
			}
			SoundEvent soundEvent = cradle.isFull() ? ModSoundEvents.CRADLE_BECAME_FULL.get() : ModSoundEvents.CRADLE_EAT.get();
			SoundUtil.broadcastBlockSound((ServerLevel) level, pos, soundEvent);
			return true;
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
				SoundUtil.clientPlayBlockSound(level, pos, ModSoundEvents.CRADLE_CRAFTING_RANDOM, 0.85f);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		CompoundTag tag = BlockItem.getBlockEntityData(stack);
		if (tag == null) return;

		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

		int primalEnergy = getPrimalEnergy(tag);
		boolean hasPrimalEnergy = primalEnergy > 0;
		boolean hasTributes = tag.contains(PrimordialCradleBlockEntity.SACRIFICE_KEY);
		boolean hasProcGenValues = tag.contains(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY);

		if (hasProcGenValues) {
			tooltip.add(ComponentUtil.emptyLine());
			tooltip.add(ComponentUtil.literal("Seeded with:").withStyle(TextStyles.PRIMORDIAL_RUNES_MUTED_PURPLE));

			MoundShape.ProcGenValues procGenValues = MoundShape.ProcGenValues.readFrom(tag.getCompound(PrimordialCradleBlockEntity.PROC_GEN_VALUES_KEY));

			tooltip.add(
					ComponentUtil.literal(df.format(procGenValues.biomeTemperature()))
							.withStyle(TextStyles.PRIMORDIAL_RUNES_PURPLE)
							.append(ComponentUtil.literal(" Temperature").withStyle(TextStyles.PRIMORDIAL_RUNES_MUTED_PURPLE))
			);
			tooltip.add(
					ComponentUtil.literal(df.format(procGenValues.biomeHumidity()))
							.withStyle(TextStyles.PRIMORDIAL_RUNES_PURPLE)
							.append(ComponentUtil.literal(" Humidity").withStyle(TextStyles.PRIMORDIAL_RUNES_MUTED_PURPLE))
			);

			if (!hasPrimalEnergy) tooltip.add(ComponentUtil.emptyLine());
		}

		if (hasPrimalEnergy) {
			tooltip.add(ComponentUtil.emptyLine());
			tooltip.add(
					ComponentUtil.literal(df.format(primalEnergy)).withStyle(TextStyles.PRIMORDIAL_RUNES_PURPLE)
							.append(ComponentUtil.literal(" Primal Energy").withStyle(TextStyles.PRIMORDIAL_RUNES_MUTED_PURPLE))
			);

			if (!hasTributes) tooltip.add(ComponentUtil.emptyLine());
		}

		if (hasTributes) {
			tooltip.add(ComponentUtil.emptyLine());

			CompoundTag sacrificeTag = tag.getCompound(PrimordialCradleBlockEntity.SACRIFICE_KEY);
			byte biomass = sacrificeTag.getByte("Biomass");
			int lifeEnergy = sacrificeTag.getInt("LifeEnergy");
			int success = sacrificeTag.getInt("Success");
			int disease = sacrificeTag.getInt("Disease");
			int hostile = sacrificeTag.getInt("Hostile");
			int anomaly = sacrificeTag.getInt("Anomaly");

			if (biomass > 0) tooltip.add(createValueComponent(df, biomass, "Biomass"));
			if (lifeEnergy > 0) tooltip.add(createValueComponent(df, lifeEnergy, "Life Energy"));
			if (success > 0) tooltip.add(createValueComponent(df, success, "Success"));
			if (disease > 0) tooltip.add(createValueComponent(df, disease, "Disease"));
			if (hostile != 0) tooltip.add(createValueComponent(df, hostile, "Hostile"));
			if (anomaly > 0) tooltip.add(createValueComponent(df, anomaly, "Anomaly"));
		}
	}
}
