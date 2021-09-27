package com.github.elenterius.biomancy.capabilities;

import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
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

	public static class LargeSingleItemHandlerProvider implements ICapabilitySerializable<INBT> {

		private final short slotSize;
		private IItemHandler cachedItemHandler;
		private final LazyOptional<IItemHandler> lazySupplier = LazyOptional.of(this::getCachedItemHandler);

		public LargeSingleItemHandlerProvider(short slotSizeIn) {
			slotSize = slotSizeIn;
		}

		private IItemHandler getCachedItemHandler() {
			if (cachedItemHandler == null) cachedItemHandler = new LargeSingleItemStackHandler(slotSize);
			return cachedItemHandler;
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == null) return LazyOptional.empty(); //mitigates NPE on startup
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, lazySupplier);
		}

		@Override
		public INBT serializeNBT() {
			INBT inbt = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(getCachedItemHandler(), null);
			return inbt == null ? new CompoundNBT() : inbt;
		}

		@Override
		public void deserializeNBT(INBT nbt) {
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(getCachedItemHandler(), null, nbt);
		}
	}

}
