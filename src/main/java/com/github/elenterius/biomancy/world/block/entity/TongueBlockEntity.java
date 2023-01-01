package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.world.LevelUtil;
import com.github.elenterius.biomancy.world.block.TongueBlock;
import com.github.elenterius.biomancy.world.inventory.itemhandler.SingleItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class TongueBlockEntity extends SimpleSyncedBlockEntity implements IAnimatable {

	public static final String INVENTORY_TAG = "Inventory";
	public static final int ITEM_TRANSFER_AMOUNT = 3;
	public static final int DURATION = 24;
	public static final int DELAY = 8 + 1; //ceil(31.2) --> 32

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

		if (ticks % DURATION == 0 && !inventory.isEmpty()) {
			Direction facing = TongueBlock.getFacing(state);
			dropItems(level, pos, facing);
			return;
		}

		if (ticks % (DURATION + DELAY) == 0 && inventory.isEmpty()) {
			Direction facing = TongueBlock.getFacing(state);
			BlockPos relativePos = pos.relative(facing.getOpposite());
			if (level.isLoaded(relativePos)) {
				LevelUtil.getItemHandler(level, relativePos, Direction.DOWN).ifPresent(this::tryToExtractItems);
			}

			if (!inventory.isEmpty()) ticks = 0;
		}
	}

	private void tryToExtractItems(IItemHandler itemHandler) {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			ItemStack stackInSlot = itemHandler.getStackInSlot(i);
			if (!stackInSlot.isEmpty()) {
				ItemStack stackInSlotCopy = stackInSlot.copy();
				stackInSlotCopy.setCount(ITEM_TRANSFER_AMOUNT);
				int amount = ITEM_TRANSFER_AMOUNT - inventory.insertItem(0, stackInSlotCopy, true).getCount();
				if (amount > 0) {
					ItemStack stack = itemHandler.extractItem(i, amount, false);
					if (!stack.isEmpty()) {
						inventory.insertItem(0, stack, false);
						setChanged();
						syncToClient();
						break;
					}
				}
			}
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

	public void dropContainerContents(Level level, BlockPos pos) {
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.getStack());
	}

	private <E extends BlockEntity & IAnimatable> PlayState handleAnim(AnimationEvent<E> event) {
		if (inventory.isEmpty()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("tongue.anim.none", EDefaultLoopTypes.LOOP));
		}
		else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("tongue.anim.stretch"));
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
