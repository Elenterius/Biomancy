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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

public class TongueBlockEntity extends BlockEntity {

	public static final int ITEM_TRANSFER_AMOUNT = 16;
	private final SingleItemStackHandler inventory;
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

	public static void serverTick(Level level, BlockPos pos, BlockState state, TongueBlockEntity sac) {
		sac.ticks++;
		if (sac.ticks % 20L == 0L) {
			sac.serverTick((ServerLevel) level, pos, state);
		}
	}

	private void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(SacBlock.FACING);
		BlockPos relativePos = pos.relative(facing.getOpposite());
		if (level.isLoaded(relativePos)) {
			LevelUtil.getItemHandler(level, relativePos, Direction.DOWN).ifPresent(this::tryToExtractItems);
		}
		dropItems(level, pos, facing);
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
						break;
					}
				}
			}
		}
	}

	private void dropItems(ServerLevel level, BlockPos pos, Direction facing) {
		ItemStack stack = inventory.extractItem(0, ITEM_TRANSFER_AMOUNT, false);
		if (!stack.isEmpty()) {
			double x = (pos.getX() + 0.5d) - facing.getStepX() * 0.5d + facing.getStepX() * (4d / 16d);
			double y = (pos.getY() + 0.5d) - facing.getStepY() * 0.5d + facing.getStepY() * (4d / 16d);
			double z = (pos.getZ() + 0.5d) - facing.getStepZ() * 0.5d + facing.getStepZ() * (4d / 16d);
			LevelUtil.dropItemStack(level, x, y, z, facing, 0.05f, stack);
		}
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

	public void dropContainerContents(Level level, BlockPos pos) {
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.getStack());
	}

}
