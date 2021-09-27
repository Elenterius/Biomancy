package com.github.elenterius.biomancy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.UUID;

public interface IOwnableItem {
	String NBT_KEY = "OwnerUUID";

	default Optional<UUID> getOwner(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.hasUUID(NBT_KEY)) {
			return Optional.of(nbt.getUUID(NBT_KEY));
		}
		return Optional.empty();
	}

	default void setOwner(ItemStack stack, UUID uuid) {
		stack.getOrCreateTag().putUUID(NBT_KEY, uuid);
	}

	default void removeOwner(ItemStack stack) {
		stack.getOrCreateTag().remove(NBT_KEY);
	}

	default boolean hasOwner(ItemStack stack) {
		return stack.getOrCreateTag().hasUUID(NBT_KEY);
	}

	default boolean isOwner(ItemStack stack, UUID uuid) {
		return getOwner(stack).map(value -> value.equals(uuid)).orElse(false);
	}
}
