package com.github.elenterius.blightlings.capabilities;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class InventoryProviders {
	private InventoryProviders() {}

	public static class ItemStackInvProvider implements ICapabilityProvider {

		public static final ResourceLocation REGISTRY_KEY = new ResourceLocation(BlightlingsMod.MOD_ID, "single_item_bag");
		private ItemStack stack; //cheese cap sync
		private final LazyOptional<IItemHandler> capProvider = LazyOptional.of(() -> new SingleItemStackHandler(stack, (short) (64 * 64)));

		public ItemStackInvProvider(ItemStack stack) {
			this.stack = stack;
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == null) return LazyOptional.empty(); //mitigates NPE on startup
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, capProvider);
		}
	}

}
