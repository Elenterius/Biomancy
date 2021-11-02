package com.github.elenterius.biomancy.capabilities;

import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class InventoryProviders {

	private InventoryProviders() {}

	public static class LargeSingleItemHandlerProvider implements ICapabilitySerializable<CompoundNBT> {

		private final short maxSlotSize;
		private LargeSingleItemStackHandler cachedItemHandler;
		private final LazyOptional<IItemHandler> lazySupplier = LazyOptional.of(this::getCachedItemHandler);

		public LargeSingleItemHandlerProvider(short slotSize) {
			maxSlotSize = slotSize;
		}

		private LargeSingleItemStackHandler getCachedItemHandler() {
			if (cachedItemHandler == null) cachedItemHandler = new LargeSingleItemStackHandler(maxSlotSize);
			return cachedItemHandler;
		}

		public static LargeSingleItemStackHandler getItemHandler(ItemStack stack) {
			IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(new LargeSingleItemStackHandler((short) 1));
			return (LargeSingleItemStackHandler) itemHandler;
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return lazySupplier.cast();
			return LazyOptional.empty();
		}

		@Override
		public CompoundNBT serializeNBT() {
			//Note: don't use ITEM_HANDLER_CAPABILITY, it's only capable of saving ItemStacks (byte overflow issue)
			return getCachedItemHandler().serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			//Note: don't use ITEM_HANDLER_CAPABILITY, it's only capable of restoring ItemStacks
			getCachedItemHandler().deserializeNBT(nbt);
		}

	}

}
