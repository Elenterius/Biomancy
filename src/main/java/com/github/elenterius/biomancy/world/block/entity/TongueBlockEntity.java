package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.world.LevelUtil;
import com.github.elenterius.biomancy.world.block.SacBlock;
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
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TongueBlockEntity extends SimpleSyncedBlockEntity implements IAnimatable {

	public static final int ITEM_TRANSFER_AMOUNT = 3;
	private final SingleItemStackHandler inventory;
	private int ticks;

	private final AnimationFactory animationFactory = new AnimationFactory(this);

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

	public static void serverTick(Level level, BlockPos pos, BlockState state, TongueBlockEntity sac) {
		sac.ticks++;
		if (sac.ticks % 24L == 0L) {
			sac.serverTick((ServerLevel) level, pos, state);
		}
	}

	private void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
		if (!inventory.isEmpty()) {
			Direction facing = state.getValue(SacBlock.FACING);
			dropItems(level, pos, facing);
			return;
		}

		Direction facing = state.getValue(SacBlock.FACING);
		BlockPos relativePos = pos.relative(facing.getOpposite());
		if (level.isLoaded(relativePos)) {
			LevelUtil.getItemHandler(level, relativePos, Direction.DOWN).ifPresent(this::tryToExtractItems);
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
			double y = pos.getY() + 2d / 16d;
			double z = (pos.getZ() + 0.5d) - facing.getStepZ() * 0.5d + facing.getStepZ() * (11d / 16d);
			ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
			itemEntity.setDefaultPickUpDelay();
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
		tag.put("Inventory", inventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		tag.put("Inventory", inventory.serializeNBT());
	}

	public void dropContainerContents(Level level, BlockPos pos) {
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.getStack());
	}

	private <E extends BlockEntity & IAnimatable> PlayState handleAnim(AnimationEvent<E> event) {
		if (inventory.isEmpty()) {
			event.getController().transitionLengthTicks = 4;
			event.getController().setAnimation(new AnimationBuilder().addAnimation("tongue.anim.none", true));
		}
		else {
			event.getController().transitionLengthTicks = 0;
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
