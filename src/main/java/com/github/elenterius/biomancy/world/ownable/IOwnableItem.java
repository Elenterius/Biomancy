package com.github.elenterius.biomancy.world.ownable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public interface IOwnableItem {
	String NBT_KEY = "OwnerUUID";

	default Optional<UUID> getOwner(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
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
