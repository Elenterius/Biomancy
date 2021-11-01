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
		private final ItemStack cachedHostStack;
		private LargeSingleItemStackHandler cachedItemHandler;
		private final LazyOptional<IItemHandler> lazySupplier = LazyOptional.of(this::getCachedItemHandler);

		public LargeSingleItemHandlerProvider(short slotSize, ItemStack hostStack) {
			maxSlotSize = slotSize;
			cachedHostStack = hostStack;
		}

		private LargeSingleItemStackHandler getCachedItemHandler() {
			if (cachedItemHandler == null) cachedItemHandler = new LargeSingleItemStackHandler(maxSlotSize) {
				@Override
				public void onContentsChanged() {
					CompoundNBT nbt = cachedHostStack.getOrCreateTag();
					nbt.putInt("CapSyncCheese", nbt.getInt("CapSyncCheese") + 1);
				}
			};
			return cachedItemHandler;
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == null) return LazyOptional.empty(); //mitigates NPE on startup
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, lazySupplier);
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
