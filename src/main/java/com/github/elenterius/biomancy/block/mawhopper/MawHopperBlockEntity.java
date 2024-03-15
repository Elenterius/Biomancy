package com.github.elenterius.biomancy.block.mawhopper;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.property.DirectedConnection;
import com.github.elenterius.biomancy.block.property.VertexType;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.inventory.itemhandler.EnhancedItemHandler;
import com.github.elenterius.biomancy.inventory.itemhandler.SingleItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Predicate;

public class MawHopperBlockEntity extends BlockEntity implements GeoBlockEntity {

	public static final String INVENTORY_TAG = "Inventory";
	public static final int ITEM_TRANSFER_AMOUNT = 16;
	public static final int DURATION = 11;
	public static final int DELAY = 8 + 1;

	public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = entity ->
			entity.isAlive()
					&& (EntitySelector.CONTAINER_ENTITY_SELECTOR.test(entity) || entity instanceof Player)
					&& entity.getCapability(ModCapabilities.ITEM_HANDLER, null).isPresent();

	protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("maw_hopper.idle");
	protected static final RawAnimation PUMPING_ANIM = RawAnimation.begin().thenLoop("maw_hopper.pumping");

	private final SingleItemStackHandler inventory;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private int ticks = BiomancyMod.GLOBAL_RANDOM.nextInt(DURATION); //add random tick offset

	private LazyOptional<IItemHandler> optionalItemHandler;

	public MawHopperBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.MAW_HOPPER.get(), pos, blockState);
		inventory = new SingleItemStackHandler() {
			@Override
			protected void onContentsChanged() {
				setChanged();
			}
		};
		optionalItemHandler = LazyOptional.of(() -> inventory);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MawHopperBlockEntity entity) {
		entity.serverTick((ServerLevel) level, pos, state);
	}

	private static LazyOptional<IItemHandler> getItemHandler(ServerLevel level, BlockPos pos, @Nullable Direction direction) {
		BlockState state = level.getBlockState(pos);
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity != null) {
				LazyOptional<IItemHandler> capability = blockEntity.getCapability(ModCapabilities.ITEM_HANDLER, direction);
				if (capability.isPresent()) return capability;
			}
		}

		List<Entity> list = level.getEntities((Entity) null, new AABB(pos), CONTAINER_ENTITY_SELECTOR);
		if (!list.isEmpty()) {
			int index = level.random.nextInt(list.size());
			return list.get(index).getCapability(ModCapabilities.ITEM_HANDLER, direction);
		}

		return LazyOptional.empty();
	}

	public static void entityInside(Level level, BlockPos pos, BlockState state, MawHopperBlockEntity blockEntity, Entity entity) {
		if (level.isClientSide) return;

		if (entity instanceof ItemEntity itemEntity) {
			AABB aabb = new AABB(pos.relative(MawHopperBlock.getConnection(state).ingoing));
			if (aabb.intersects(entity.getBoundingBox())) {
				addItem(blockEntity.inventory, itemEntity);
			}
		}
	}

	private static boolean addItem(SingleItemStackHandler handler, ItemEntity itemEntity) {
		ItemStack copy = itemEntity.getItem().copy();
		int oldCount = copy.getCount();

		ItemStack remainder = handler.insertItem(copy, false);

		if (remainder.isEmpty()) {
			itemEntity.discard();
			return true;
		}

		if (remainder.getCount() != oldCount) {
			itemEntity.setItem(remainder);
			return true;
		}

		return false;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == ModCapabilities.ITEM_HANDLER) return optionalItemHandler.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		optionalItemHandler.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		optionalItemHandler = LazyOptional.of(() -> inventory);
	}

	private void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
		ticks++;

		if (ticks % DURATION == 0 && !inventory.isEmpty()) {
			DirectedConnection connection = MawHopperBlock.getConnection(state);
			BlockPos insertPos = pos.relative(connection.outgoing);
			if (level.isLoaded(insertPos)) {
				LazyOptional<IItemHandler> itemHandler = getItemHandler(level, insertPos, connection.outgoing.getOpposite());
				itemHandler.map(this::tryToInsertItems);
			}
		}

		if (ticks % (DURATION + DELAY) == 0 && !inventory.isFull() && isMawHead()) {
			BlockPos pullPos = pos.relative(MawHopperBlock.getConnection(state).ingoing);
			if (level.isLoaded(pullPos)) {
				LazyOptional<IItemHandler> itemHandler = getItemHandler(level, pullPos, Direction.DOWN);
				if (!itemHandler.map(this::tryToExtractItems).orElse(false)) {
					pullItemEntities(level, pullPos);
				}
			}
		}
	}

	private boolean isMawHead() {
		return MawHopperBlock.getVertexType(getBlockState()) == VertexType.SOURCE;
	}

	private boolean tryToInsertItems(IItemHandler itemHandler) {
		ItemStack stack = inventory.extractItem(ITEM_TRANSFER_AMOUNT, false);
		if (stack.isEmpty()) return false;
		int oldCount = stack.getCount();

		EnhancedItemHandler handler = new EnhancedItemHandler(itemHandler);
		ItemStack remainder = handler.insertItem(stack, false);

		boolean success = remainder.getCount() != oldCount;

		if (!remainder.isEmpty()) {
			inventory.insertItem(remainder, false);
		}

		return success;
	}

	private boolean tryToExtractItems(IItemHandler itemHandler) {
		if (inventory.isFull()) return false;

		EnhancedItemHandler handler = new EnhancedItemHandler(itemHandler);

		int amount = Math.min(ITEM_TRANSFER_AMOUNT, inventory.getMaxAmount() - inventory.getAmount());
		Predicate<ItemStack> canAcceptItem = stack -> inventory.insertItem(stack, true).isEmpty();
		ItemStack extractedStack = handler.extractItemFirstMatch(canAcceptItem, amount, false);

		if (!extractedStack.isEmpty()) {
			inventory.insertItem(extractedStack, false);
			return true;
		}

		return false;
	}

	private void pullItemEntities(ServerLevel level, BlockPos pos) {
		List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos), EntitySelector.ENTITY_STILL_ALIVE);
		for (ItemEntity itemEntity : entities) {
			if (addItem(inventory, itemEntity)) return;
		}
	}

	public void dropInventoryContents(Level level, BlockPos pos) {
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.extractItem(inventory.getMaxAmount(), false));
	}

	public void giveInventoryContentsTo(Level level, BlockPos pos, Player player) {
		ItemStack stack = inventory.extractItem(inventory.getMaxAmount(), false);
		if (!stack.isEmpty() && !player.addItem(stack)) {
			player.drop(stack, false);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put(INVENTORY_TAG, inventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound(INVENTORY_TAG));
	}

	private <E extends MawHopperBlockEntity> PlayState handleAnim(AnimationState<E> event) {
		//		if (inventory.isEmpty()) {
		//			event.getController().setAnimation(IDLE_ANIM);
		//		}
		//		else {
		//			event.getController().setAnimation(PUMPING_ANIM);
		//		}
		event.getController().setAnimation(PUMPING_ANIM);
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "main", 0, this::handleAnim));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

}
