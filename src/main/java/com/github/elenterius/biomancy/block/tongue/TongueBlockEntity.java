package com.github.elenterius.biomancy.block.tongue;

import com.github.elenterius.biomancy.block.base.SimpleSyncedBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.inventory.itemhandler.EnhancedItemHandler;
import com.github.elenterius.biomancy.inventory.itemhandler.SingleItemStackHandler;
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
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class TongueBlockEntity extends SimpleSyncedBlockEntity implements IAnimatable {

	public static final String INVENTORY_TAG = "Inventory";
	public static final int ITEM_TRANSFER_AMOUNT = 3;
	public static final int DURATION = 24;
	public static final int DELAY = 12;
	protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().loop("tongue.idle");
	protected static final AnimationBuilder STRETCH_ANIM = new AnimationBuilder().playOnce("tongue.stretch");

	private final SingleItemStackHandler inventory;
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

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

		EnhancedItemHandler handler = new EnhancedItemHandler(itemHandler);
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

	protected <T extends TongueBlockEntity> PlayState handleAnim(AnimationEvent<T> event) {
		AnimationController<T> controller = event.getController();
		boolean isStopped = controller.getAnimationState() == AnimationState.Stopped;

		if (event.getAnimatable().isHoldingItem()) {
			controller.setAnimation(STRETCH_ANIM);
			if (!isStopped) return PlayState.CONTINUE;
		}

		if (isStopped) {
			controller.setAnimation(IDLE_ANIM);
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::handleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

}
