package com.github.elenterius.biomancy.block.tongue;

import com.github.elenterius.biomancy.block.base.SimpleSyncedBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.inventory.ItemHandlerWrapper;
import com.github.elenterius.biomancy.inventory.SingleItemStackHandler;
import com.github.elenterius.biomancy.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TongueBlockEntity extends SimpleSyncedBlockEntity implements GeoBlockEntity {

	public static final String INVENTORY_TAG = "Inventory";
	public static final int ITEM_TRANSFER_AMOUNT = 3;
	public static final int DURATION = 24;
	public static final int DELAY = 12;
	protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("tongue.idle");
	protected static final RawAnimation STRETCH_ANIM = RawAnimation.begin().thenPlay("tongue.stretch");

	private final SingleItemStackHandler inventory;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private int ticks;

	public TongueBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.TONGUE.get(), pos, state);
		inventory = new SingleItemStackHandler() {
			@Override
			public int getSlotLimit(int slot) {
				return ITEM_TRANSFER_AMOUNT;
			}

			@Override
			protected void onContentsChanged() {
				setChanged();
			}
		};
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TongueBlockEntity entity) {
		entity.serverTick((ServerLevel) level, pos, state);
	}

	private void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
		ticks++;

		if (ticks % DURATION == 0 && isHoldingItem()) {
			Direction facing = TongueBlock.getFacing(state);
			dropItems(level, pos, facing);
			return;
		}

		if (ticks % (DURATION + DELAY) == 0 && !isHoldingItem()) {
			Direction facing = TongueBlock.getFacing(state);
			BlockPos relativePos = pos.relative(facing.getOpposite());
			if (level.isLoaded(relativePos)) {
				LevelUtil.getItemHandler(level, relativePos, Direction.DOWN).ifPresent(this::tryToExtractItems);
			}

			if (isHoldingItem()) ticks = 0;
		}
	}

	private void tryToExtractItems(IItemHandler itemHandler) {
		if (!inventory.isEmpty()) return;

		ItemHandlerWrapper handler = new ItemHandlerWrapper(itemHandler);
		ItemStack extractedStack = handler.extractItemAny(ITEM_TRANSFER_AMOUNT, false);
		if (!extractedStack.isEmpty()) {
			inventory.insertItem(0, extractedStack, false);
			setChanged();
			syncToClient();
		}
	}

	private void dropItems(ServerLevel level, BlockPos pos, Direction facing) {
		ItemStack stack = inventory.extractItem(0, ITEM_TRANSFER_AMOUNT, false);
		if (!stack.isEmpty()) {
			double x = (pos.getX() + 0.5d) - facing.getStepX() * 0.5d + facing.getStepX() * (11d / 16d);
			double y = pos.getY() + 1d / 16d;
			double z = (pos.getZ() + 0.5d) - facing.getStepZ() * 0.5d + facing.getStepZ() * (11d / 16d);
			ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
			itemEntity.setDefaultPickUpDelay();
			itemEntity.setDeltaMovement(0, 0, 0);
			level.addFreshEntity(itemEntity);
			syncToClient();
		}
	}

	public ItemStack getHeldItem() {
		return inventory.getStack();
	}

	public boolean isHoldingItem() {
		return !inventory.isEmpty();
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

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		tag.put(INVENTORY_TAG, inventory.serializeNBT());
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

	protected <T extends TongueBlockEntity> PlayState handleAnimationState(AnimationState<T> state) {
		AnimationController<T> controller = state.getController();
		boolean isStopped = controller.getAnimationState() == AnimationController.State.STOPPED;

		if (state.getAnimatable().isHoldingItem()) {
			controller.setAnimation(STRETCH_ANIM);
			if (!isStopped) return PlayState.CONTINUE;
		}

		if (isStopped) {
			controller.setAnimation(IDLE_ANIM);
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, this::handleAnimationState));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

}
