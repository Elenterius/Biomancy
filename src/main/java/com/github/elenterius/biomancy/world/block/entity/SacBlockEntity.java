package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.block.SacBlock;
import com.github.elenterius.biomancy.world.inventory.SacMenu;
import com.github.elenterius.biomancy.world.inventory.SimpleInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SacBlockEntity extends CustomContainerBlockEntity {

	public static final int SLOTS = 5;
	public static final int ITEM_TRANSFER_AMOUNT = 8;
	private final SimpleInventory<?> inventory;
	private int ticks;

	public SacBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SAC.get(), pos, state);
		inventory = SimpleInventory.createServerContents(SLOTS, this::canPlayerOpenContainer, this::setChanged);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SacBlockEntity sac) {
		sac.ticks++;
		if (sac.ticks % 20L == 0L) {
			sac.serverTick((ServerLevel) level, pos, state);
		}
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return SacMenu.createServerMenu(containerId, playerInventory, inventory);
	}

	private void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
		if (!inventory.isFull()) {
			BlockPos relativePos = pos.relative(state.getValue(SacBlock.FACING).getOpposite());
			if (level.isLoaded(relativePos)) {
				getItemHandler(level, relativePos, Direction.DOWN).ifPresent(this::tryToExtractItems);
			}
		}
	}

	private void tryToExtractItems(IItemHandler itemHandler) {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			ItemStack stackInSlot = itemHandler.getStackInSlot(i);
			if (!stackInSlot.isEmpty()) {
				ItemStack stackInSlotCopy = stackInSlot.copy();
				stackInSlotCopy.setCount(ITEM_TRANSFER_AMOUNT);
				int amount = ITEM_TRANSFER_AMOUNT - inventory.insertItemStack(stackInSlotCopy, true).getCount();
				if (amount > 0) {
					ItemStack stack = itemHandler.extractItem(i, amount, false);
					if (!stack.isEmpty()) {
						inventory.insertItemStack(stack);
						setChanged();
						break;
					}
				}
			}
		}
	}

	private LazyOptional<IItemHandler> getItemHandler(ServerLevel level, BlockPos pos, Direction direction) {
		BlockState state = level.getBlockState(pos);
		if (state.hasBlockEntity()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity != null) {
				return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
			}
		}
		return LazyOptional.empty();
	}

	public Component getDefaultName() {
		return TextComponentUtil.getTranslationText("container", "sac");
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
	public void dropContainerContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, inventory);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventory.getOptionalItemHandlerWithBehavior().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		inventory.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		inventory.revive();
	}

}
